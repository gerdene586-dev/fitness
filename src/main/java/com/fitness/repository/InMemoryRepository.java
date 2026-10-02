package com.fitness.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<T> implements CrudRepository<T> {

    private final Map<Long, T> data = new LinkedHashMap<>();

    private long idCounter = 1;

    @Override
    public T save(T entity) {
        return entity;
    }

    public T save(long id, T entity) {
        data.put(id, entity);
        return entity;
    }

    @Override
    public T update(T entity) {
        return entity;
    }

    public T update(long id, T entity) {
        data.put(id, entity);
        return entity;
    }

    @Override
    public void delete(long id) {
        data.remove(id);
    }

    @Override
    public Optional<T> findById(long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public long nextId() {
        return idCounter++;
    }

    public void put(long id, T entity) {
        data.put(id, entity);

        if (id >= idCounter) {
            idCounter = id + 1;
        }
    }

    public int size() {
        return data.size();
    }
}