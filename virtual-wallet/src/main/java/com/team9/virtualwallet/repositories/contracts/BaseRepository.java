package com.team9.virtualwallet.repositories.contracts;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BaseRepository<E> {
    List<E> getAll(Pageable pageable);

    E getById(int id);

    <V> E getByField(String fieldName, V value);

    <V> List<E> getByFieldList(String fieldName, V value);

    <V> List<E> searchByFieldList(String fieldName, V value);

    void create(E obj);

    void update(E obj);

    void delete(E obj);
}
