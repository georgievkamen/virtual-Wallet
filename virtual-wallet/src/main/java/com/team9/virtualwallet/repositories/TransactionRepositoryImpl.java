package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Pages;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.Sort;
import com.team9.virtualwallet.repositories.contracts.TransactionRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepositoryImpl extends BaseRepositoryImpl<Transaction> implements TransactionRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TransactionRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Transaction.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Pages<Transaction> getAll(User user, Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transaction> query = session.createQuery("from Transaction where sender.id = :id or recipient.id = :id order by timestamp desc", Transaction.class);
            query.setParameter("id", user.getId());
            query.setFirstResult((pageable.getPageSize() * pageable.getPageNumber()) - pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());

            Query countQuery = session.createQuery("select count (id) from Transaction where sender.id = :id or recipient.id = :id");
            countQuery.setParameter("id", user.getId());
            Long countResults = (Long) countQuery.uniqueResult();

            return new Pages<>(query.list(), countResults, pageable);
        }
    }

    @Override
    public List<Transaction> getLastTransactions(User user, int count) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transaction> query = session.createQuery("from Transaction where sender.id = :id or recipient.id = :id order by timestamp desc", Transaction.class);
            query.setParameter("id", user.getId());
            query.setMaxResults(count);
            return query.list();
        }
    }

    @Override
    public void create(Transaction transaction, Wallet walletToWithdraw, Wallet walletToDeposit) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(walletToDeposit);
            session.update(walletToWithdraw);
            session.save(transaction);
            session.getTransaction().commit();
        }
    }

    @Override
    public void createExternal(Transaction transaction, Wallet wallet) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(wallet);
            session.save(transaction);
            session.getTransaction().commit();
        }
    }

    @Override
    public Pages<Transaction> filter(int userId,
                                     Optional<Direction> direction,
                                     Optional<Date> startDate,
                                     Optional<Date> endDate,
                                     Optional<Integer> searchedPersonId,
                                     Optional<Sort> amount,
                                     Optional<Sort> date,
                                     Pageable pageable) {

        try (Session session = sessionFactory.openSession()) {
            var baseQuery = "from Transaction";
            var countBaseQuery = "select count (id) ";
            List<String> filters = new ArrayList<>();
            List<String> sortType = new ArrayList<>();

            if (direction.isPresent()) {
                switch (direction.get().toString()) {
                    case "Incoming":
                        if (searchedPersonId.isPresent()) {
                            filters.add("sender.id = :searchedId");
                        }
                        addToFiltersIfIncomingOrOutgoing(filters);
                        filters.add("recipient.id = :userId");
                        break;
                    case "Outgoing":
                        if (searchedPersonId.isPresent()) {
                            filters.add("recipient.id = :searchedId");
                        }
                        addToFiltersIfIncomingOrOutgoing(filters);
                        filters.add("sender.id = :userId");
                        break;

                    case "Deposit":
                        filters.add("recipient.id = :userId");
                        filters.add("transactionType = 'CARD_TO_WALLET'");
                        break;

                    case "Withdraw":
                        filters.add("recipient.id = :userId");
                        filters.add("transactionType = 'WALLET_TO_CARD'");
                        break;

                    case "Wallet to Wallet":
                        filters.add("recipient.id = :userId");
                        filters.add("transactionType = 'WALLET_TO_WALLET'");
                        break;
                }
            } else {
                filters.add("(sender.id = :userId or recipient.id = :userId)");
            }

            if (startDate.isPresent()) {
                filters.add(" timestamp > :startDate");
            }

            if (endDate.isPresent()) {
                filters.add(" timestamp < :endDate");
            }


            amount.ifPresent(sortAmount -> sortType.add(String.format(" Amount %s ", sortAmount)));
            date.ifPresent(sortDate -> sortType.add(String.format(" timestamp %s ", sortDate)));

            baseQuery += " where " + String.join(" and ", filters);

            if (!sortType.isEmpty()) {
                baseQuery += " order by " + String.join(" , ", sortType);
            }

            countBaseQuery += baseQuery;

            Query<Transaction> query = session.createQuery(baseQuery, Transaction.class);
            Query countQuery = session.createQuery(countBaseQuery);

            query.setParameter("userId", userId);
            countQuery.setParameter("userId", userId);

            startDate.ifPresent(value -> {
                query.setParameter("startDate", value);
                countQuery.setParameter("startDate", value);
            });

            endDate.ifPresent(value -> {
                query.setParameter("endDate", value);
                countQuery.setParameter("endDate", value);
            });

            searchedPersonId.ifPresent(integer -> {
                query.setParameter("searchedId", integer);
                countQuery.setParameter("searchedId", integer);
            });


            query.setFirstResult((pageable.getPageSize() * pageable.getPageNumber()) - pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());
            Long countResults = (Long) countQuery.uniqueResult();

            return new Pages<>(query.list(), countResults, pageable);
        }
    }

    private void addToFiltersIfIncomingOrOutgoing(List<String> filters) {
        filters.add("transactionType != 'WALLET_TO_WALLET'");
        filters.add("transactionType != 'CARD_TO_WALLET'");
        filters.add("transactionType != 'WALLET_TO_CARD'");
    }
}
