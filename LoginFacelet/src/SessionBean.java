import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

@ManagedBean(name = "sessionBean")
@SessionScoped
 public class SessionBean implements Serializable{
    private String login;
    private String password;

    public SessionBean() {
            login = "guest";
            password = "guest";
    }

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
        boolean res = database.isValidUser(login,password);

        return res;
    }
}
