import dataclasses.User;

import java.io.Serializable;
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

    public void fresh(){
        Database database = new Database();
        arrayList = database.showUserDB();
    }
}
