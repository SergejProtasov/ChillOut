package web;

import web.dataclasses.DatabaseConnection;
import web.dataclasses.Salt;
import web.dataclasses.User;

import java.io.Serializable;
import java.sql.*;

public class SessionBean implements Serializable {
    private String login = null;
    private String password = null;

    public SessionBean() {}

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

    public boolean searchUser() {
        Connection connection = DatabaseConnection.setConnection();

        try {
            String select = "SELECT * FROM users where login = ?";
            System.out.println(select);
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1,login);
            statement.execute();

            ResultSet set = statement.getResultSet();
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

    public void exit(){
        login = null;
        password = null;
    }

    public void fresh(){}
}
