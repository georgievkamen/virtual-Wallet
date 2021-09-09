package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.repositories.contracts.BaseRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;


import java.util.List;

public class BaseRepositoryImpl<E> implements BaseRepository<E> {

    private final SessionFactory sessionFactory;
    private final Class<E> clazz;

    public BaseRepositoryImpl(SessionFactory sessionFactory, Class<E> clazz) {
        this.sessionFactory = sessionFactory;
        this.clazz = clazz;
    }

    @Override
    public List<E> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<E> query = session.createQuery("from " + clazz.getSimpleName(), clazz);
            return query.list();
        }
    }

    @Override
    public E getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            E obj = session.get(clazz, id);
            if (obj == null) {
                throw new EntityNotFoundException(clazz.getSimpleName(), id);
            }
            return obj;
        }
    }

    @Override
    public <V> E getByField(String fieldName, V value) {
        List<E> list = getByFieldList(fieldName, value);
        if (list.isEmpty()) {
            throw new EntityNotFoundException(clazz.getSimpleName(), fieldName, String.valueOf(value));
        }
        return list.get(0);
    }

    @Override
    public <V> List<E> getByFieldList(String fieldName, V value) {
        String query = String.format("from %s where %s = :value", clazz.getSimpleName(), fieldName);
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(query, clazz)
                    .setParameter("value", value).list();
        }
    }

    @Override
    public <V> List<E> searchByFieldList(String fieldName, V value) {
        String query = String.format("from %s where %s like :value", clazz.getSimpleName(), fieldName);
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(query, clazz)
                    .setParameter("value", "%" + value + "%").list();
        }
    }

    @Override
    public void create(E obj) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(obj);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(E obj) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(obj);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int id) {
        E obj = getById(id);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(obj);
            session.getTransaction().commit();
        }
    }
}