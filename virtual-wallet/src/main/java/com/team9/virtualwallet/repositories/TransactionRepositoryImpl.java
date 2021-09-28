package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.SortAmount;
import com.team9.virtualwallet.models.enums.SortDate;
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
    public List<Transaction> getAll(User user, Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            Query<Transaction> query = session.createQuery("from Transaction where sender.id = :id or recipient.id = :id order by timestamp desc", Transaction.class);
            query.setParameter("id", user.getId());
            query.setFirstResult((pageable.getPageSize() * pageable.getPageNumber()) - pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());
            return query.list();
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
    public List<Transaction> filter(int userId,
                                    Direction direction,
                                    Optional<Date> startDate,
                                    Optional<Date> endDate,
                                    Optional<Integer> searchedPersonId,
                                    Optional<SortAmount> amount,
                                    Optional<SortDate> date,
                                    Pageable pageable) {

        try (Session session = sessionFactory.openSession()) {
            var baseQuery = "select t from Transaction t";
            List<String> filters = new ArrayList<>();
            List<String> sortType = new ArrayList<>();


            switch (direction.toString()) {
                case "Incoming":
                    if (searchedPersonId.isPresent()) {
                        filters.add("t.sender.id = :searchedId");
                    }
                    filters.add("t.recipient.id = :userId");
                    break;
                case "Outgoing":
                    if (searchedPersonId.isPresent()) {
                        filters.add("t.recipient.id = :searchedId");
                    }
                    filters.add("t.sender.id = :userId");
                    break;
            }

            if (startDate.isPresent()) {
                filters.add(" t.timestamp > :startDate");
            }

            if (endDate.isPresent()) {
                filters.add(" t.timestamp < :endDate");
            }


            amount.ifPresent(sortAmount -> sortType.add(String.format(" %s ", sortAmount)));
            date.ifPresent(sortDate -> sortType.add(String.format(" %s ", sortDate.toString().replaceAll("Date", "timestamp"))));

            baseQuery += " where " + String.join(" and ", filters);

            if (!sortType.isEmpty()) {
                baseQuery += " order by " + String.join(" , ", sortType);
            }

            Query<Transaction> query = session.createQuery(baseQuery, Transaction.class);
            query.setParameter("userId", userId);

            startDate.ifPresent(value -> query.setParameter("startDate", value));
            endDate.ifPresent(value -> query.setParameter("endDate", value));
            searchedPersonId.ifPresent(integer -> query.setParameter("searchedId", integer));
            query.setFirstResult((pageable.getPageSize() * pageable.getPageNumber()) - pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());

            return query.list();
        }

    }
}
