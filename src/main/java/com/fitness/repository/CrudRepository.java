package com.fitness.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {

    T save(T entity);

    T update(T entity);

    void delete(long id);

    Optional<T> findById(long id);

    List<T> findAll();

    long nextId();
}