package com.moukiladev.javadb.controllers;

import com.moukiladev.javadb.DbConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.*;

import static java.lang.Integer.parseInt;

public class HelloController {

    @FXML
    private TextArea resultTrace;
    @FXML
    private TextField designation;
    @FXML
    private TextField price;



    @FXML
    protected void onRegisterButtonClick() throws IOException, SQLException {

        String createTable = "CREATE TABLE IF NOT EXISTS GOODS (" +
                                "ID BIGINT AUTO_INCREMENT PRIMARY KEY," +
                                "DESIGNATION VARCHAR(125)," +
                                "PRICE BIGINT)";

        String Designation = designation.getText();
        int Price = parseInt(price.getText());

        String insertGoods = "INSERT INTO GOODS (DESIGNATION, PRICE) VALUES (?, ?)";

        try (Connection conn = getTheConnection()) {

            System.out.println("Database connection successful");
            //appendText() allows you to build up the content of the TextArea over multiple calls.
            resultTrace.appendText("Database connection successful.\n");

            // Create a statement and execute SQL queries
            Statement stmt = conn.createStatement();
            // Create table if not exists
            stmt.execute(createTable);

            // Use PreparedStatement for inserting data and avoid sql injection
            try(PreparedStatement pstmt = conn.prepareStatement(insertGoods)){
                pstmt.setString(1, Designation);
                pstmt.setInt(2, Price);
                pstmt.executeUpdate();
                System.out.println("Data recording successful");
                resultTrace.appendText("Data recording successful.\n");
            }
            clearInputs();

            ResultSet rs = stmt.executeQuery("SELECT * FROM GOODS");

            while (rs.next()) {
                System.out.println(rs.getString("ID") + " - " +rs.getString("DESIGNATION") + " - "
                        + rs.getString("PRICE") + " FCFA");
            }

        }catch (SQLException e){
            e.printStackTrace();
            resultTrace.appendText(e.getMessage());
        }

    }

    @FXML
    protected void onResetButtonClick() throws IOException{
        clearInputs();
    }

    public void clearInputs(){
        designation.clear();
        price.clear();

    }

    private Connection getTheConnection() throws SQLException {
        String url = "jdbc:h2:~/src/main/resources/com/moukiladev/Database/stock.db";
        String user ="JavaDB";
        String password = "123456789";
        DbConnection dbConnection = new DbConnection(url, user, password);

        return dbConnection.connect();
    }

}