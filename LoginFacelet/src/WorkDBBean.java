import dataclasses.User;

import java.io.Serializable;

public class WorkDBBean implements Serializable{
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

    public boolean startWorkAdd(){
        if(!password.equals(confPassword)){
            return false;
        }

        Database database = new Database();
        User user = new User(firstname,lastname,login,password,null);
        return database.addUser(user);

    }

    public void startWorkDel(){
        Database database = new Database();
        database.deleteUser(firstname,lastname);
    }
}
