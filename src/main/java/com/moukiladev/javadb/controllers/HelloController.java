package com.moukiladev.javadb.controllers;

import com.moukiladev.javadb.DbConnection;
import com.moukiladev.javadb.Goods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class HelloController implements Initializable {

    @FXML
    private TextArea resultTrace;
    @FXML
    private TextField designation;
    @FXML
    private TextField price;
    @FXML
    private TableView<Goods> tableGoods;
    @FXML
    private TableColumn<Goods, Integer> columnId;
    @FXML
    private TableColumn<Goods, String> columnDesignation;
    @FXML
    private TableColumn<Goods, Integer> columnPrice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnDesignation.setCellValueFactory(new PropertyValueFactory<>("designation"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        loadGoodsFromDatabase();
    }

    private void loadGoodsFromDatabase() {
        List<Goods> listdbGoods = new ArrayList<>();

        try(Connection conn = getTheConnection()){
            Statement stmt = conn.createStatement();
            getResultGoods(stmt, listdbGoods);
        } catch (SQLException e) {
            e.printStackTrace();
            resultTrace.appendText("Error when loading data "+e.getMessage()+"\n");
        }

    }

    @FXML
    protected void onRegisterButtonClick() {
        String Designation = designation.getText();
        int Price = parseInt(price.getText());
        Goods myGoods = new Goods(Designation, Price);

        String createTable = "CREATE TABLE IF NOT EXISTS GOODS (" +
                                "ID BIGINT AUTO_INCREMENT PRIMARY KEY," +
                                "DESIGNATION VARCHAR(125)," +
                                "PRICE BIGINT,"+
                                "UNIQUE (DESIGNATION, PRICE))";

        String insertGoods = "INSERT INTO GOODS (DESIGNATION, PRICE) VALUES (?, ?)";

        try (Connection conn = getTheConnection()) {
            // Create a statement and execute SQL queries
            Statement stmt = conn.createStatement();
            // Create table if not exists
            stmt.execute(createTable);
            // Use PreparedStatement for inserting data and avoid sql injection
            try(PreparedStatement pstmt = conn.prepareStatement(insertGoods)){
                pstmt.setString(1, myGoods.getDesignation());
                pstmt.setInt(2, myGoods.getPrice());
                pstmt.executeUpdate();
                //appendText() allows you to build up the content of the TextArea over multiple calls.
                resultTrace.setStyle("-fx-text-fill: green;");
                resultTrace.appendText("Goods saved successfully !\n");
                List<Goods> ResultGoods = new ArrayList<>();
                clearInputs();
                //Reloading goods
                getResultGoods(stmt, ResultGoods);

            }catch (SQLException e){
                // Handling duplicated goods exceptions
                if(e.getMessage().contains("Unique index or primary key violation")){
                    resultTrace.setStyle("-fx-text-fill: red;");
                    resultTrace.appendText("Duplicated goods not allowed"+e.getMessage()+"\n");
                }
            }



        }catch (SQLException e){
            e.printStackTrace();
            resultTrace.setStyle("-fx-text-fill: red;");
            resultTrace.appendText("Error when loading data "+e.getMessage()+"\n");
        }


    }

    private void getResultGoods(Statement stmt, List<Goods> ResultGoods) throws SQLException {
        String selectGoods = "SELECT * FROM GOODS";
        ResultSet rs = stmt.executeQuery(selectGoods);

        while (rs.next()) {
            System.out.println(rs.getString("ID") + " - " +rs.getString("DESIGNATION") + " - "
                    + rs.getString("PRICE") + " FCFA");

            int theId = rs.getInt("ID");
            String theDesignation = rs.getString("DESIGNATION");
            int thePrice = rs.getInt("PRICE");

            Goods theGoods = new Goods(theId, theDesignation, thePrice);
            ResultGoods.add(theGoods);
        }
        System.out.println(ResultGoods);
        updateTableView(ResultGoods);
    }

    @FXML
    protected void onResetButtonClick() {
        clearInputs();
    }

    protected Connection getTheConnection() throws SQLException {
        String url = "jdbc:h2:~/src/main/resources/com/moukiladev/Database/stock.db";
        String user ="JavaDB";
        String password = "123456789";
        DbConnection dbConnection = new DbConnection(url, user, password);

        return dbConnection.connect();
    }

    protected void clearInputs(){
        designation.clear();
        price.clear();

    }

    protected void updateTableView(List<Goods> resultGoods){
        ObservableList<Goods> observableGoods = FXCollections.observableArrayList(resultGoods);
        tableGoods.setItems(observableGoods);
    }
}