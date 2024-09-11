package com.moukiladev.javadb.controllers;

import com.moukiladev.javadb.DbConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;

import static java.lang.Integer.parseInt;

public class HelloController {

    @FXML
    private TextField goodsID;
    @FXML
    private TextField designation;
    @FXML
    private TextField price;



    @FXML
    protected void onRegisterButtonClick() throws IOException, SQLException {
        /*
        //String url = "jdbc:h2:~/stock.mv.db";
        String url = "jdbc:h2:~/src/main/resources/com/moukiladev/Database/stock.mv.db";
        String user ="JavaDB";
        String password = "123456789";

        */
        String createTable = "CREATE TABLE IF NOT EXISTS GOODS (ID INT PRIMARY KEY, Designation VARCHAR(125), PRICE long)";
        int GoodsID = parseInt(goodsID.getText());
        String Designation = designation.getText();
        String Price = price.getText();

        String insertGoods = "INSERT INTO GOODS VALUES ("+GoodsID+", '"+Designation+"',"+Price+")";

        try (Connection conn = getTheConnection()) {
            // Create a statement and execute SQL queries
            Statement stmt = conn.createStatement();
            stmt.execute(createTable);
            stmt.execute(insertGoods);
            clearInputs();

            ResultSet rs = stmt.executeQuery("SELECT * FROM GOODS");

            while (rs.next()) {
                System.out.println(rs.getInt("ID") + " - "
                        + rs.getString("Designation") + " - "
                        + rs.getString("PRICE") + " FCFA");
            }
            //conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @FXML
    protected void onResetButtonClick() throws IOException{
        clearInputs();
    }

    public void clearInputs(){
        goodsID.clear();
        designation.clear();
        price.clear();

    }

    private Connection getTheConnection() throws SQLException {
        String url = "jdbc:h2:~/src/main/resources/com/moukiladev/Database/stock.mv.db";
        String user ="JavaDB";
        String password = "123456789";
        DbConnection dbConnection = new DbConnection(url, user, password);

        System.out.println("Connection established");
        return dbConnection.connect();
    }

}