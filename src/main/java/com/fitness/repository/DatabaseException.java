package com.fitness.repository;

public class DatabaseException extends RuntimeException {

    public DatabaseException(String operation, String table, Throwable cause) {
        super("Өгөгдлийн сангийн " + operation + " үйлдэл амжилтгүй боллоо (" + table + ").", cause);
    }
}
