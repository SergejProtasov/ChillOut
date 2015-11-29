package web;

import dataclasses.connections.DataProperties;
import dataclasses.connections.DatabaseConnection;
import dataclasses.web.Salt;
import dataclasses.web.User;

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
        String tUser = DataProperties.getProp("users");
        String column1 = DataProperties.getProp("users.firstname");
        String column2 = DataProperties.getProp("users.lastname");
        String column3 = DataProperties.getProp("users.login");
        String column4 = DataProperties.getProp("users.password");
        String column5 = DataProperties.getProp("users.salt");

        try {
            String select = "SELECT * FROM "+tUser+" where "+column3+" = ?";
            System.out.println(select);
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1,login);
            statement.execute();

            ResultSet set = statement.getResultSet();
            User user = null;
            while (set.next()) {
                user = new User();
                user.setUser(set.getString(column1),
                            set.getString(column2),
                            set.getString(column3),
                            set.getString(column4),
                            set.getString(column5));
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
}
