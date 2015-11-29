package dataclasses;

import java.sql.*;
import java.sql.Connection;

public class DatabaseConnection {
    private static Connection connection = null;
    private static DatabaseConnection databaseConnection = null;

    private DatabaseConnection() {
        String rLogin = DataProperties.getProp("db.login");
        String rPassword = DataProperties.getProp("db.password");
        String URL = DataProperties.getProp("db.host");
        String driver = DataProperties.getProp("db.driver");

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(URL, rLogin, rPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("DatabaseConnection isn't reached.");
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