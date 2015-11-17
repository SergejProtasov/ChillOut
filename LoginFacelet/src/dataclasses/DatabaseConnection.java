package dataclasses;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.sql.*;
import java.sql.Connection;

public class DatabaseConnection {
    private String rLogin = "root";
    private String rPassword = "root";
    private String URL = "jdbc:mysql://localhost:3306/ids";

    private Connection connection;
    private static DatabaseConnection databaseConnection = new DatabaseConnection();

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, rLogin, rPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("dataclasses.DatabaseConnection isn't reached.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance(){
        return databaseConnection;
    }

    public Connection getConnection(){
        return connection;
    }

}