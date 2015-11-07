
import org.apache.commons.codec.digest.DigestUtils;


import java.sql.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;

public class DatabaseBean {
    private String rLogin = "root";
    private String rPassword = "root";
    private String URL = "jdbc:mysql://localhost:3306/ids";

    protected DatabaseBean() {
    }

    protected DatabaseBean(String rLogin, String rPassword, String URL) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, rLogin, rPassword);
            connection.close();
            this.rLogin = rLogin;
            this.rPassword = rPassword;
            this.URL = URL;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database isn't reached.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private final ResultSet createQuery(Statement statement) throws SQLException {
        String tblname = "users";
        String query = "SELECT * FROM users";

        ResultSet set = statement.executeQuery(query);
        return set;
    }

    private final User saulting(User user) {
        String login = DigestUtils.sha1Hex(user.getSault()+user.getLogin());
        String password = DigestUtils.sha1Hex(user.getPassword()+user.getSault());
        user.setLogin(login);
        user.setPassword(password);
        return user;
    }

    private final User searchUser(String login, String password){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL,rLogin,rPassword);

            Statement statement = connection.createStatement();
            ResultSet set = createQuery(statement);
            User user1 = null;

            while(set.next()){
                user1 = new User(set.getString("firstName"),set.getString("lastName"), set.getString("login"), set.getString("passwrd"),set.getString("sault"));

                User user2 = user1;
                user2.setLogin(login);
                user2.setPassword(password);
                if(user1.equals(saulting(user2))){
                    break;
                }
            }
            set.close();
            statement.close();
            connection.close();
            return user1;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        catch (NullPointerException k){
            k.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected boolean isValidUser(String login, String password){
        //String shalogin = DigestUtils.sha1Hex(login);
        //String shapassword = DigestUtils.sha1Hex(password);
        User res = searchUser(login,password);
        return (res != null);
    }

    private void insertUser(Connection connection, User user) throws SQLException {
        String insert = "INSERT INTO users VALUES(?,?,?,?,?)";
        Random random = new Random();
        user.setSault(random.nextLong());
        User res = saulting(user);

        PreparedStatement preparedStatement = connection.prepareStatement(insert);
        preparedStatement.setString(1,res.getFirstName());
        preparedStatement.setString(2,res.getLastName());
        preparedStatement.setString(3,res.getLogin());
        preparedStatement.setString(4,res.getPassword());
        preparedStatement.setString(5,res.getSault());

        preparedStatement.execute();
        preparedStatement.close();
    }

    protected void addUser(User user){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL,rLogin,rPassword);

            insertUser(connection,user);
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void deleteUser(){

    }
}
