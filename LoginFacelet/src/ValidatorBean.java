import java.io.Serializable;

 public class ValidatorBean implements Serializable{
    private String login;
    private String password;

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

    public boolean result(){
        Database database = new Database();
        return database.isValidUser(login,password);
    }
}
