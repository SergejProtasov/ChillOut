import java.io.Serializable;

/**
 * Created by Дом on 03.11.2015.
 */
public class ValidatorBean implements Serializable{
    //private String uLogin = "Lock";
    //private String uPassword = "admin";

    //private String rLogin;
    //private String rPassword;
    //private String URL;

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
        DatabaseBean databaseBean = new DatabaseBean("root", "root", "jdbs:mysql://localhost:3306/ids");
        return databaseBean.isValidUser(login,password);
    }
}
