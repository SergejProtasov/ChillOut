
import org.apache.commons.codec.digest.DigestUtils;


import java.sql.*;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseBean{
    private String rLogin = "root";
    private String rPassword = "root";
    private String URL = "jdbc:mysql://localhost:3306/ids";

    protected DatabaseBean(){}
    protected DatabaseBean(String rLogin, String rPassword, String URL) {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL,rLogin,rPassword);
            connection.close();
            this.rLogin = rLogin;
            this.rPassword = rPassword;
            this.URL = URL;
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database isn't reached.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private final ResultSet createQuery(Statement statement) throws SQLException{
        String tblname = "users";
        String query = "select * from users";

        ResultSet set = statement.executeQuery(query);
        return set;
    }

    protected boolean isValidUser(String login, String password){
        //String shalogin = DigestUtils.sha1Hex(login);
        //String shapassword = DigestUtils.sha1Hex(password);
        String shalogin = login;
        String shapassword = password;
        int count = 0;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL,rLogin,rPassword);

            Statement statement = connection.createStatement();
            ResultSet set = createQuery(statement);

            while(set.next()){
                if(set.getString("login").equals(shalogin) && set.getString("passwrd").equals(shapassword)) {
                    count++;
                }
            }
            statement.close();
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        catch (NullPointerException k){
            k.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return (count == 1);
    }

    protected void addUser(){

    }

    protected void deleteUser(){

    }
}
