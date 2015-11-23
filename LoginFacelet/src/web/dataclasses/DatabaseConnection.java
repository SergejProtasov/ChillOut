package web.dataclasses;

import java.sql.*;
import java.sql.Connection;

public class DatabaseConnection {
    private String rLogin = "root";
    private String rPassword = "root";
    private String URL = "jdbc:mysql://localhost:3306/ids";

    private static Connection connection = null;
    private static DatabaseConnection databaseConnection = null;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, rLogin, rPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("web.dataclasses.DatabaseConnection isn't reached.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection(){
        return connection;
    }

    public static Connection setConnection() {
        if(connection == null){
            databaseConnection = new DatabaseConnection();
            connection = databaseConnection.getConnection();
        }
        return connection;
    }

}