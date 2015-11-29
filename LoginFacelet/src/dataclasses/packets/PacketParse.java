package dataclasses.packets;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class PacketParse {
    public static String parse(String data, PreparedStatement preparedStatement, int n, int offset) throws SQLException {
        for(int i = 0; i < n;i++) {
            data = data.substring(data.indexOf(": ")+2);
            int ind = data.indexOf("\n");
            ind = (data.contains("\r") && ind >= 0 )? data.indexOf("\r"): ind;
            int k = data.indexOf(" ");
            ind = (k < ind && k > 0)? k: ind;
            String s1 = data.substring(0,ind);
            preparedStatement.setString(offset+i,s1);
        }
        int k = data.indexOf("\n");
        if(k >= 0 && data.length() > k+2) {
            data = data.substring(k+2);
        }
        return data;
    }

}
