package com.moukiladev.javadb.controllers;

import com.moukiladev.javadb.DbConnection;
import com.moukiladev.javadb.Goods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class HelloController implements Initializable {

    @FXML
    private ScrollPane logScrollPane;
    @FXML
    private TextFlow resultTrace;
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

    /**
     * Initializes the controller after the root element has been completely processed.
     * Sets up table columns and loads existing goods data from the database.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnDesignation.setCellValueFactory(new PropertyValueFactory<>("designation"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        loadGoodsFromDatabase();
        //resultTrace.setStyle("-fx-background-color: white;");
    }

    /**
     * Loads all goods from the database and populates the table view.
     * Handles SQL exceptions and logs error messages in the result area.
     */
    private void loadGoodsFromDatabase() {
        List<Goods> listdbGoods = new ArrayList<>();

        try(Connection conn = getTheConnection()){
            Statement stmt = conn.createStatement();
            getResultGoods(stmt, listdbGoods);
        } catch (SQLException e) {
            e.printStackTrace();
            appendLogTrace("Error when loading data ",e.getMessage(), "red");
        }

    }

    /**
     * Triggered when the Register button is clicked.
     * Inserts a new goods record into the database after ensuring the table exists.
     * Uses PreparedStatement to prevent SQL injection and avoid duplicates.
     */
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

        try (Connection conn = getTheConnection(); PreparedStatement pstmt = conn.prepareStatement(insertGoods)) {
            // Create a statement and execute SQL queries
            Statement stmt = conn.createStatement();
            // Create table if not exists
            stmt.execute(createTable);
            // Use PreparedStatement for inserting data and avoid sql injection
            pstmt.setString(1, myGoods.getDesignation());
            pstmt.setInt(2, myGoods.getPrice());
            pstmt.executeUpdate();
            appendResult("Good saved successfully !","green");
            appendResult("Entry : "+myGoods.getDesignation(),"green");
            List<Goods> ResultGoods = new ArrayList<>();
            clearInputs();
            //Reloading goods
            getResultGoods(stmt, ResultGoods);

        }catch(SQLException e){
        // Handling duplicated goods exceptions
            if(e.getMessage().contains("Unique index or primary key violation")){
                appendLogTrace("Duplicated goods not allowed",e.getMessage(), "red");
            }else{
                appendLogTrace("Unknow error occured",e.getMessage(), "red");
            }
            e.printStackTrace();
            appendLogTrace("Error when loading data ", e.getMessage(), "red");
        }
    }

    @FXML
    protected void onKeyPressed(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER){
            onRegisterButtonClick();
        }
    }

    /**
     * Triggered when the Update button is clicked.
     * Updates the selected goods record in the database with new values from input fields.
     * Displays messages based on the success or failure of the operation.
     */
    @FXML
    protected void onUpdateButtonClick() throws SQLException{
        Goods selectedGood = tableGoods.getSelectionModel().getSelectedItem();

        if (selectedGood == null ){
            appendResult("No goods selected for update","red");
        }

        String newDesignation = designation.getText();
        int newPrice = parseInt(price.getText());

        String updateGoods = "UPDATE GOODS SET DESIGNATION = ?, PRICE = ? WHERE ID = ?";
        try(Connection conn = getTheConnection(); PreparedStatement pstmt = conn.prepareStatement(updateGoods)){
            pstmt.setString(1, newDesignation);
            pstmt.setInt(2, newPrice);
            pstmt.setLong(3, selectedGood.getId());

            int rowsAffected = pstmt.executeUpdate();
            if(rowsAffected > 0){
                appendResult("update successfully", "green");
                appendResult("Good : "+newDesignation, "green");
                loadGoodsFromDatabase();

            }else{
                appendResult("Update failed, no rows affected", "red");
            }
        }catch(SQLException e){
            e.printStackTrace();
            appendLogTrace("Error when updating Data", e.getMessage(), "red");
            appendLogTrace("Good : "+newDesignation, e.getMessage(), "red");
        }

    }

    @FXML
    protected void onDeleteButtonClick() throws SQLException{
        Goods selectedGood = tableGoods.getSelectionModel().getSelectedItem();
        if (selectedGood == null ){
            appendResult("No goods selected for update","red");
        }
        String deleteGoods = "DELETE goods WHERE id=?";
        try(Connection conn = getTheConnection(); PreparedStatement pstmt = conn.prepareStatement(deleteGoods)){
            pstmt.setLong(1, selectedGood.getId());
            pstmt.executeUpdate();
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                appendResult("Deleted: " + selectedGood.getDesignation(), "green");
            } else {
                appendResult("Delete failed: no rows affected", "orange");
            }
            loadGoodsFromDatabase(); // Refresh the table
        } catch (SQLException e) {
            appendResult("Error deleting goods: " + e.getMessage(), "red");
            e.printStackTrace(); // For development — use a logger in production
        }
    }
    /**
     * Retrieves all goods from the database using a given Statement.
     * Populates a list of Goods objects and updates the table view.
     */
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

    /**
     * Triggered when the Reset button is clicked.
     * Clears the input fields for designation and price.
     */
    @FXML
    protected void onResetButtonClick() {
        clearInputs();
    }

    /**
     * Returns a Connection object to the H2 database using custom DbConnection class.
     */
    protected Connection getTheConnection() throws SQLException {
        String url = "jdbc:h2:~/src/main/resources/com/moukiladev/Database/stock.db";
        String user ="JavaDB";
        String password = "123456789";
        DbConnection dbConnection = new DbConnection(url, user, password);

        return dbConnection.connect();
    }

    /**
     * Clears the input fields for goods designation and price.
     */
    protected void clearInputs(){
        designation.clear();
        price.clear();
    }

    /**
     * Updates the TableView with a fresh list of goods retrieved from the database.
     * This method takes a standard Java List of Goods objects and wraps it in an ObservableList
     * so that JavaFX can monitor the data and automatically reflect changes in the UI.
     */
    protected void updateTableView(List<Goods> resultGoods){
        // Convert the regular List to an ObservableList to allow JavaFX to observe and react to changes.
        ObservableList<Goods> observableGoods = FXCollections.observableArrayList(resultGoods);
        // The TableView requires an ObservableList to dynamically update its display
        // when the underlying data changes — e.g., additions, deletions, or edits.
        tableGoods.setItems(observableGoods);
    }

    /**
     * Appends an exception log message to the resultTrace area with color styling.
     */
    protected void appendLogTrace(String message, String ExceptionMessage, String color){
        Text text =new Text(message+ExceptionMessage+"\n");
        text.setStyle("-fx-fill: "+color+";");
        addScroll(text);
    }

    /**
     * Appends a standard result message to the resultTrace area with color styling.
     */
    protected void appendResult(String message, String color){
        Text text =new Text(message+"\n");
        text.setStyle("-fx-fill: "+color+";");
        addScroll(text);
    }

    /**
     * Adds the given text to the scrollable result area and scrolls to the bottom automatically.
     */
    protected void addScroll(Text text){
        resultTrace.getChildren().add(text);
        resultTrace.layout();
        logScrollPane.setVvalue(1.0);
    }

}