/**
 * Created by Дом on 05.11.2015.
 */

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

public class DatabaseBean{
    private String rLogin = "root";
    private String rPassword = "root";
    private String URL = "jdbs:mysql://localhost:3306/ids/users";

    protected DatabaseBean(){}
    protected DatabaseBean(String rLogin, String rPassword, String URL) {
        try{

            //Connection connection = driver.connect(URL+"?"+"user="+rLogin+"&"+"password="+rPassword,null);
            Connection connection = DriverManager.getConnection(URL,rLogin,rPassword);
            //MysqlDataSource dataSource = new MysqlDataSource();
            //dataSource.setUser(rLogin);
            //dataSource.setPassword(rPassword);
            //dataSource.setURL(URL);
            //connection = dataSource.getConnection();
            if(connection.isClosed()){
                throw new SQLException();
            }
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("Database isn't reached.");
        }
        this.rLogin = rLogin;
        this.rPassword = rPassword;
        this.URL = URL;
    }

    private final ResultSet createQuery(Statement statement, String login, String password) throws SQLException{
        String tblname = URL.substring(URL.lastIndexOf("/")+1);
        String query = "select * from "+tblname+" where login = "+login+" and passwrd = " + password;

        ResultSet set = statement.executeQuery(query);
        return set;
    }

    protected boolean isValidUser(String login, String password){
        String shalogin = DigestUtils.sha1Hex(login);
        String shapassword = DigestUtils.sha1Hex(password);


        int count = 0;

        try{
            Connection connection;
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(rLogin);
            dataSource.setPassword(rPassword);
            dataSource.setURL(URL);
            connection = dataSource.getConnection();

            Statement statement = connection.createStatement();
            ResultSet set = createQuery(statement,shalogin,shapassword);
            statement.close();
            connection.close();

            while(set.next()){
                count ++;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        catch (NullPointerException k){
            k.printStackTrace();
            return false;
        }

        return (count == 1);
    }

    protected void addUser(){

    }

    protected void deleteUser(){

    }
}
