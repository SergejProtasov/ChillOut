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
        DatabaseBean databaseBean = new DatabaseBean();
        return databaseBean.isValidUser(login,password);
    }
}
