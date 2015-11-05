/**
 * Created by Дом on 05.11.2015.
 */

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseBean {
    private final String rLogin;
    private final String rPassword;
    private final String URL;

    protected DatabaseBean(String rLogin, String rPassword, String URL) {
        try{
            Connection connection;
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(rLogin);
            dataSource.setPassword(rPassword);
            dataSource.setURL(URL);
            connection = dataSource.getConnection();
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        this.rLogin = rLogin;
        this.rPassword = rPassword;
        this.URL = URL;
    }

    private final String createQuery(String login, String password){

        return null;
    }

    protected boolean isValidUser(String login, String password){
        String shalogin = DigestUtils.sha1Hex(login);
        String shapassword = DigestUtils.sha1Hex(password);

        ResultSet set;
        int count = 0;

        String query = createQuery(shalogin,shapassword);
        try{
            Connection connection;
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(rLogin);
            dataSource.setPassword(rPassword);
            dataSource.setURL(URL);
            connection = dataSource.getConnection();

            Statement statement = connection.createStatement();
            set = statement.executeQuery(query);
            statement.close();
            connection.close();

            while(set.next()){//size table
                count ++;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }

        return (count == 1);
    }

    protected void addUser(){

    }

    protected void deleteUser(){

    }
}
