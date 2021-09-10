package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.Sort;
import com.team9.virtualwallet.repositories.contracts.TransactionRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Transaction> getAll(User user) {
        try (Session session = sessionFactory.openSession()) {
            //TODO Check if it works correctly
            Query<Transaction> query = session.createQuery("from Transaction where sender.id = :id or recipient.id = :id", Transaction.class);
            query.setParameter("id", user.getId());
            return query.list();
        }
    }

    @Override
    public List<Transaction> filter(int userId,
                                    Optional<Date> startDate,
                                    Optional<Date> endDate,
                                    Optional<Integer> senderId,
                                    Optional<Integer> recipientId,
                                    Optional<Direction> direction,
                                    Optional<String> amount,
                                    Optional<String> date,
                                    Sort sort) {

        try (Session session = sessionFactory.openSession()) {
            var baseQuery = "select t from Transaction t ";
            List<String> filters = new ArrayList<>();
            List<String> sortType = new ArrayList<>();

            if (startDate.isPresent()) {
                filters.add(" t.timestamp > :startDate");
            }

            if (endDate.isPresent()) {
                filters.add(" t.timestamp < :endDate");
            }

            if (senderId.isPresent()) {
                filters.add("t.sender.id = :senderId");
            }

            if (recipientId.isPresent()) {
                filters.add("t.recipient.id = :recipientId");
            }

            if (direction.isPresent()) {
                switch (direction.toString()) {
                    case "Incoming":
                        filters.add("t.sender.id != :userId");
                        break;
                    case "Outgoing":
                        filters.add("t.sender.id = userId");
                        break;
                }
            }

            if (amount.isPresent()) {
                sortType.add(" t.amount ");
            }

            if (date.isPresent()) {
                sortType.add(" t.timestamp ");
            }

            if (!filters.isEmpty()) {
                baseQuery += " where " + String.join(" and ", filters);
            }

            if (!sortType.isEmpty()) {
                baseQuery += " order by " + String.join(" then by ", sortType);
                baseQuery += String.format(" %s ", sort);
            }

            Query<Transaction> query = session.createQuery(baseQuery, Transaction.class);

            startDate.ifPresent(value -> query.setParameter("startDate", value));
            endDate.ifPresent(value -> query.setParameter("endDate", value));
            senderId.ifPresent(integer -> query.setParameter("senderId", integer));
            recipientId.ifPresent(integer -> query.setParameter("recipientId", integer));
            direction.ifPresent(integer -> query.setParameter("userId", userId));

            return query.list();
        }

    }
}
