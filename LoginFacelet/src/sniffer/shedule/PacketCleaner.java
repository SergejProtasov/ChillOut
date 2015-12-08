package sniffer.shedule;

import dataclasses.connections.DataProperties;
import dataclasses.connections.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PacketCleaner {
    public static void cleanDB(){
        Connection connection = DatabaseConnection.setConnection();
        Integer n = new Integer(DataProperties.getProp("tables"));
        String[] tables = new String[n];
        for (int i =0; i < n; i++ ){
            tables[i] = DataProperties.getProp("tables"+(i+1));
        }

        String status = DataProperties.getProp("status");
        String clnpacket = DataProperties.getProp("status.warning");
        for(int i = 0; i < n; i++) {
            try {
                String delete = "DELETE FROM " + tables[i] + " WHERE " + status + " = " + clnpacket;
                PreparedStatement preparedStatement = connection.prepareStatement(delete);
                preparedStatement.execute();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
