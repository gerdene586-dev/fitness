package com.fitness;

import com.fitness.repository.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.sql.SQLException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            DatabaseConnection.verifyConnection();
        } catch (IllegalStateException | SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Өгөгдлийн сангийн холболт");
            alert.setHeaderText("MySQL өгөгдлийн санд холбогдож чадсангүй.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            stage.close();
            return;
        }

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/fitness/main.fxml")
        );

        Scene scene = new Scene(loader.load(), 1400, 850);

        scene.getStylesheets().add(
                MainApp.class.getResource("/com/fitness/style.css").toExternalForm()
        );

        stage.setTitle("Фитнесс төвийн удирдлагын систем");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
        System.out.println("Фитнесс төвийн систем амжилттай эхэллээ.");
        System.out.println("MySQL өгөгдлийн сантай амжилттай холбогдлоо.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}