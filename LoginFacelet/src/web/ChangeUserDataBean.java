package web;

import dataclasses.DatabaseConnection;
import dataclasses.Salt;
import dataclasses.User;

import java.io.Serializable;
import java.security.SecureRandom;
import java.sql.*;

public class ChangeUserDataBean implements Serializable{
    private String firstname;
    private String lastname;
    private String login;
    private String password;
    private String confPassword;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfPassword() {
        return confPassword;
    }

    public void setConfPassword(String confPassword) {
        this.confPassword = confPassword;
    }

    private void insertUser(Connection connection, User user) throws SQLException {
        String insert = "INSERT INTO users VALUES(?,?,?,?,?)";
        SecureRandom random = new SecureRandom();
        user.setSault(random.nextLong());
        Salt.salting(user);

        PreparedStatement preparedStatement = connection.prepareStatement(insert);
        preparedStatement.setString(1,user.getFirstName());
        preparedStatement.setString(2,user.getLastName());
        preparedStatement.setString(3,user.getLogin());
        preparedStatement.setString(4,user.getPassword());
        preparedStatement.setString(5,user.getSault());

        preparedStatement.execute();
        preparedStatement.close();
    }

    public boolean addUser(){
        if(!password.equals(confPassword)){
            return false;
        }

        User user = new User(firstname,lastname,login,password,null);
        Connection connection = DatabaseConnection.setConnection();

        try {
            insertUser(connection,user);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteUser(){
        Connection connection = DatabaseConnection.setConnection();

        try{
            String delete = "Delete from users where firstName = ? and lastName = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setString(1,firstname);
            preparedStatement.setString(2,lastname);
            preparedStatement.execute();

            preparedStatement.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
