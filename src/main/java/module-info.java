module com.fitness {

    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires mysql.connector.j;

    exports com.fitness;
    exports com.fitness.model;
    exports com.fitness.controller;
    exports com.fitness.service;
    exports com.fitness.repository;

    opens com.fitness.controller to javafx.fxml;
    opens com.fitness.model to javafx.base;
}