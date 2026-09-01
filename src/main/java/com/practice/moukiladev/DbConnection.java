package com.practice.moukiladev;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static Connection connection = null;
    private static final String url = "jdbc:h2:./database/stock";
    private static final String user = "JavaDB";
    private static final String password = "123456789";

    public static Connection getConnection() throws SQLException{
        if (connection == null){
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    public static void closeConnection() throws SQLException{
        if (connection != null && !connection.isClosed()){
            connection.close();
        }
    }
}
