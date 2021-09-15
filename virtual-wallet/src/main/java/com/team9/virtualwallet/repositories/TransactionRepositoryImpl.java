package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.enums.Direction;
import com.team9.virtualwallet.models.enums.SortAmount;
import com.team9.virtualwallet.models.enums.SortDate;
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
                                    Optional<Integer> categoryId,
                                    Optional<Integer> senderId,
                                    Optional<Integer> recipientId,
                                    Optional<Direction> direction,
                                    Optional<SortAmount> amount,
                                    Optional<SortDate> date) {

        try (Session session = sessionFactory.openSession()) {
            var baseQuery = "select t from Transaction t join Category c on t.category.id = c.id";
            List<String> filters = new ArrayList<>();
            List<String> sortType = new ArrayList<>();

            if (startDate.isPresent()) {
                filters.add(" t.timestamp > :startDate");
            }

            if (endDate.isPresent()) {
                filters.add(" t.timestamp < :endDate");
            }

            if (categoryId.isPresent()) {
                filters.add(" c.id = :categoryId");
            }

            if (senderId.isPresent()) {
                filters.add("t.sender.id = :senderId");
            }

            if (recipientId.isPresent()) {
                filters.add("t.recipient.id = :recipientId");
            }

            if (direction.isPresent()) {
                switch (direction.get().toString()) {
                    case "Incoming":
                        filters.add("t.recipient.id = :userId");
                        break;
                    case "Outgoing":
                        filters.add("t.sender.id = :userId");
                        break;
                }
            }

            amount.ifPresent(sortAmount -> sortType.add(String.format(" %s ", sortAmount)));
            date.ifPresent(sortDate -> sortType.add(String.format(" %s ", sortDate.toString().replaceAll("Date", "timestamp"))));

            if (!filters.isEmpty()) {
                baseQuery += " where " + String.join(" and ", filters);
            }

            if (!sortType.isEmpty()) {
                baseQuery += " order by " + String.join(" , ", sortType);
            }

            Query<Transaction> query = session.createQuery(baseQuery, Transaction.class);

            startDate.ifPresent(value -> query.setParameter("startDate", value));
            endDate.ifPresent(value -> query.setParameter("endDate", value));
            categoryId.ifPresent(integer -> query.setParameter("categoryId", integer));
            senderId.ifPresent(integer -> query.setParameter("senderId", integer));
            recipientId.ifPresent(integer -> query.setParameter("recipientId", integer));
            direction.ifPresent(integer -> query.setParameter("userId", userId));

            return query.list();
        }

    }
}
