package com.moukiladev.javadb;
import com.moukiladev.javadb.controllers.HelloController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void init() throws Exception {
        DbConnection firstConnection = new DbConnection("jdbc:h2:~/src/main/resources/com/moukiladev/Database/stock.db","JavaDB","123456789");
        if(firstConnection.connect().isValid(4)) {
            System.out.println("Connected to the database");
        }

    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Moukila Stock Management");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}