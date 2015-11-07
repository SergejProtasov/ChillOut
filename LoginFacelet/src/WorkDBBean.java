import java.io.Serializable;

public class WorkDBBean implements Serializable{
    private boolean startAdd = false;
    private boolean startDel = false;

    private String firstname;
    private String lastname;
    private String login;
    private String password;
    private String confPassword;

    public boolean isStartAdd() {
        return startAdd;
    }

    public void setStartAdd(boolean startAdd) {
        this.startAdd = true;
    }

    public boolean isStartDel(){
        return startDel;
    }

    public void setStartDel(boolean startDel){
        this.startDel = startDel;
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

    public void startWork(){
        if(!password.equals(confPassword)){
            return;
        }

        DatabaseBean databaseBean = new DatabaseBean();
        if(startAdd){
            User user = new User(firstname,lastname,login,password,null);
            databaseBean.addUser(user);
            startAdd = false;
        }
        if(startDel){
            databaseBean.deleteUser();
            startDel = false;
        }
    }
}
