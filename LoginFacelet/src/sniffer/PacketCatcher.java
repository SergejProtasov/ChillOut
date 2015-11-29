package sniffer;

import dataclasses.connections.DataProperties;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.sample.GetNextPacketEx;
import dataclasses.connections.DatabaseConnection;

import java.io.EOFException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.TimeoutException;

public class PacketCatcher{
    private static final int COUNT = 100000;

    private static final String READ_TIMEOUT_KEY
            = GetNextPacketEx.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT
            = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]

    private static final String SNAPLEN_KEY
            = GetNextPacketEx.class.getName() + ".snaplen";
    private static final int SNAPLEN
            = Integer.getInteger(SNAPLEN_KEY, 65536);

    private static int count = 0;

    private static void cleanDB(){
        Connection connection = DatabaseConnection.setConnection();

        String tUser = DataProperties.getProp("packets");
        //String time = DataProperties.getProp("packets.time");
        String status = DataProperties.getProp("packets.status");
        String clnpacket = DataProperties.getProp("packets.clean");

        try{
            String delete = "Delete from "+tUser+" where "+status+" = ?";/*CURRENT_DATE > "+time; */
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setString(1,clnpacket);
            preparedStatement.execute();

            preparedStatement.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void demonize() throws IOException {
        System.out.close();
        System.in.close();
    }

    private static void saveToIPv6(Packet packet){

    }

    private static void saveToIPv4(Packet packet){

    }

    private static void saveToUDP(Packet packet){

    }

    private static void saveToTCP(Packet packet){

    }

    private static void saveToDB(Packet packet,Timestamp timestamp){
        Connection connection = DatabaseConnection.setConnection();
        String packets = DataProperties.getProp("packets");
        String defaultstat = DataProperties.getProp("packets.clean");

        if(count < COUNT) {
            count++;
        } else{
            count = 1;
        }

        try {
            String insert = "INSERT INTO "+packets+" VALUES(?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            String s = packet.getHeader().toString();

            s = s.substring(s.indexOf(": ")+2);
            int ind = s.indexOf("\n");
            ind = (s.contains("\r") && ind >= 0 )? s.indexOf("\r"): ind;
            String destination = s.substring(0,ind);

            s = s.substring(s.indexOf(": ")+2);
            ind = s.indexOf("\n");
            ind = (s.contains("\r") && ind >= 0 )? s.indexOf("\r"): ind;
            String source = s.substring(0,ind);

            s = s.substring(s.indexOf(" (")+2);
            ind = s.indexOf(")");
            String type = s.substring(0,ind);

            preparedStatement.setString(1,timestamp.toString());
            preparedStatement.setString(2,defaultstat);
            preparedStatement.setString(3,destination);
            preparedStatement.setString(4,source);
            preparedStatement.setString(5,type);
            preparedStatement.setString(6,Integer.toString(count));

            preparedStatement.execute();
            preparedStatement.close();

            if(type.equals("IPv4")) {
                saveToIPv4(packet.getPayload());
            }
            if(type.equals("IPv6")) {
                saveToIPv6(packet.getPayload());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) throws PcapNativeException, NotOpenException {
        String filter = "";
        PcapNetworkInterface nif;

        try {
            nif =  NIFSelector.selectNif();
           if (nif == null) {
                return;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        PcapHandle handle
                = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);

        handle.setFilter(
                filter,
                BpfProgram.BpfCompileMode.OPTIMIZE
        );

        /*try {
            demonize();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        while (true) {
            try {
                Packet packet = handle.getNextPacketEx();
                saveToDB(packet, handle.getTimestamp());
            } catch (TimeoutException e) {
                cleanDB();
            } catch (EOFException e) {
                e.printStackTrace();
                break;
            }
        }
        handle.close();
    }
}

