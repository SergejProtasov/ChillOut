package sniffer.shedule;

import dataclasses.connections.DataProperties;
import dataclasses.connections.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class PacketStatistic {
    private static int getCountArp(){
        Connection connection = DatabaseConnection.setConnection();
        String packets = DataProperties.getProp("packets");
        String type = DataProperties.getProp("packets.type");

        try {
            String select = "Select count(*) from "+packets+" where "+type+" = \'ARP\'";
            PreparedStatement preparedStatement = connection.prepareStatement(select);

            ResultSet set = preparedStatement.executeQuery();
            return set.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int getCountIpv6(){
        Connection connection = DatabaseConnection.setConnection();
        String ipv6 = DataProperties.getProp("ipv6");
        String dest = DataProperties.getProp("ipv4.dest");
        String add = DataProperties.getProp("addv6");

        try {
            String select = "Select count(*) from "+ipv6+" where "+dest+" = "+add;
            PreparedStatement preparedStatement = connection.prepareStatement(select);

            ResultSet set = preparedStatement.executeQuery();
            return set.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int getCountIpv4(){
        Connection connection = DatabaseConnection.setConnection();
        String ipv4 = DataProperties.getProp("ipv4");
        String dest = DataProperties.getProp("ipv4.dest");
        String add = DataProperties.getProp("addv4");

        try {
            String select = "Select count(*) from "+ipv4+" where "+dest+" = "+add;
            PreparedStatement preparedStatement = connection.prepareStatement(select);

            ResultSet set = preparedStatement.executeQuery();
            return set.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int getCountPackets(){
        int count = 0;
        count += getCountArp();
        count += getCountIpv4();
        count += getCountIpv6();
        return count;
    }

    public static void getStatistic(){
        Connection connection = DatabaseConnection.setConnection();
        String stat = DataProperties.getProp("stat");

        int count = getCountPackets();
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            String insert = "INSERT INTO "+stat+" VALUES(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setString(1,date.toString());
            preparedStatement.setInt(2,count);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

