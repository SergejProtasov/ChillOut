package web;

import dataclasses.DataProperties;
import dataclasses.DatabaseConnection;
import dataclasses.User;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UserDataBean implements Serializable{
    private ArrayList<User> arrayList;

    public int amountLine(){
        freshTable();
        return arrayList.size();
    }

    public String getLine(int num){
        return (num >= 0 && num < arrayList.size())?
                arrayList.get(num).getFirstName()+" "+arrayList.get(num).getLastName():
                null;
    }

    private ArrayList<User> freshTable(){
        Connection connection = DatabaseConnection.setConnection();

        String column1 = DataProperties.getProp("users.column1");
        String column2 = DataProperties.getProp("users.column2");

        try{
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM users";
            ResultSet set =  statement.executeQuery(query);
            ArrayList<User> arrayList = new ArrayList<User>();
            User user1 = new User();

            while(set.next()){
                user1.setUser(set.getString(column1),set.getString(column2), null, null, null);
                arrayList.add(user1);
            }
            set.close();
            statement.close();
            return arrayList;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
