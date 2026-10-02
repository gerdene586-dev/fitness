package com.fitness.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class JdbcRepository<T> {

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet resultSet) throws SQLException;
    }

    private final String table;
    private final String[] columns;
    private final RowMapper<T> rowMapper;
    private final Function<T, Object[]> values;
    private final Function<T, Long> idGetter;
    private final BiConsumer<T, Long> idSetter;

    public JdbcRepository(
            String table,
            String[] columns,
            RowMapper<T> rowMapper,
            Function<T, Object[]> values,
            Function<T, Long> idGetter,
            BiConsumer<T, Long> idSetter
    ) {
        this.table = table;
        this.columns = columns.clone();
        this.rowMapper = rowMapper;
        this.values = values;
        this.idGetter = idGetter;
        this.idSetter = idSetter;
    }

    public T save(T entity) {
        String placeholders = String.join(
                ", ",
                Collections.nCopies(columns.length, "?")
        );
        String sql = "INSERT INTO " + table + " (" + String.join(", ", columns)
                + ") VALUES (" + placeholders + ")";

        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            bind(statement, values.apply(entity), 1);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Insert хийсний дараа үүссэн ID олдсонгүй.");
                }
                idSetter.accept(entity, keys.getLong(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("insert", table, e);
        }
    }

    public T update(T entity) {
        String assignments = String.join(
                ", ",
                Arrays.stream(columns)
                        .map(column -> column + " = ?")
                        .toList()
        );
        String sql = "UPDATE " + table + " SET " + assignments + " WHERE id = ?";

        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int nextIndex = bind(statement, values.apply(entity), 1);
            statement.setLong(nextIndex, idGetter.apply(entity));
            if (statement.executeUpdate() == 0) {
                throw new SQLException("ID " + idGetter.apply(entity) + " олдсонгүй.");
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("update", table, e);
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM " + table + " WHERE id = ?";

        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("delete", table, e);
        }
    }

    public Optional<T> findById(long id) {
        String sql = "SELECT id, " + String.join(", ", columns)
                + " FROM " + table + " WHERE id = ?";

        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(rowMapper.map(resultSet))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("select", table, e);
        }
    }

    public List<T> findAll() {
        String sql = "SELECT id, " + String.join(", ", columns) + " FROM " + table
                + " ORDER BY id";
        List<T> entities = new ArrayList<>();

        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                entities.add(rowMapper.map(resultSet));
            }
            return entities;
        } catch (SQLException e) {
            throw new DatabaseException("select", table, e);
        }
    }

    private static int bind(
            PreparedStatement statement,
            Object[] values,
            int firstIndex
    ) throws SQLException {
        int index = firstIndex;
        for (Object value : values) {
            statement.setObject(index++, value);
        }
        return index;
    }
}
