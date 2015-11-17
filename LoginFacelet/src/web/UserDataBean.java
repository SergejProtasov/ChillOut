package web;

import web.dataclasses.DatabaseConnection;
import web.dataclasses.User;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UserDataBean implements Serializable{
    private ArrayList<User> arrayList;

    public int amountLine(){
        fresh();
        return arrayList.size();
    }

    public String getLine(int num){
        return (num >= 0 && num < arrayList.size())? arrayList.get(num).getFirstName()+" "+arrayList.get(num).getLastName(): null;
    }

    private ArrayList<User> showUserDB(){
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        Connection connection = databaseConnection.getConnection();

        try{
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM users";
            ResultSet set =  statement.executeQuery(query);
            ArrayList<User> arrayList = new ArrayList<User>();
            while(set.next()){
                User user1 = new User(set.getString("firstName"),set.getString("lastName"), null, null, null);
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

    public void fresh(){
        arrayList = showUserDB();
    }
}
