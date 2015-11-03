/**
 * Created by Дом on 03.11.2015.
 */
public class ValidatorBean {
    private String uLogin = "Lock";
    private String uPassword = "admin";

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
        return uLogin.equals(login) && uPassword.equals(password);
    }
}
