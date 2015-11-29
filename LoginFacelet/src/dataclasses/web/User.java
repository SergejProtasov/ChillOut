package dataclasses.web;

public class User {
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private String sault = null;

    public User(){}

    public void setUser(String firstName, String lastName, String login, String password, String sault){
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.sault = sault;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getSault() {
        return sault;
    }

    public void setSault(String sault) {
        this.sault = sault;
    }

    public void setSault(Long sault){
        Long l = new Long(sault);
        this.sault = l.toString();
    }
}
