package web;

import web.dataclasses.DatabaseConnection;
import web.dataclasses.Salt;
import web.dataclasses.User;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SessionBean implements Serializable{
    private String login;
    private String password;

    public SessionBean() {
            login = "guest";
            password = "guest";
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    private boolean searchUser(String login, String password) {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        Connection connection = databaseConnection.getConnection();

        try {
            String select = "SELECT * FROM users where login = "+login;
            Statement statement = connection.createStatement();

            ResultSet set = statement.executeQuery(select);
            User user = null;
            while (set.next()) {
                user = new User(set.getString("firstName"),set.getString("lastName"),set.getString("login"),set.getString("passwrd"),set.getString("salt"));
                User loginer = user;
                loginer.setPassword(password);
                Salt.salting(loginer);
                if(loginer.equals(user)){
                    set.close();
                    statement.close();
                    return true;
                }
            }
            set.close();
            statement.close();
            return false;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean result(){
        return searchUser(login,password);
    }

    public String exit(){
        login = "guest";
        password = "guest";
        return "exit";
    }
}
