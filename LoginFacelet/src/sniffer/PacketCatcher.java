package sniffer;

import dataclasses.connections.DataProperties;
import dataclasses.packets.PacketParse;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.sample.GetNextPacketEx;
import dataclasses.connections.DatabaseConnection;

import java.io.EOFException;
import java.io.IOException;
import java.sql.*;
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

    private static void saveToIPv6(Packet packet){

    }

    private static void saveToIPv4(Packet packet){
        Connection connection = DatabaseConnection.setConnection();
        String ipv4 = DataProperties.getProp("ipv4");
        String status = DataProperties.getProp("status.warning");
        int ind;

        try {
            String insert = "INSERT INTO "+ipv4+" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            String s = packet.getHeader().toString();
            s = s.substring(s.indexOf(": ")+2);
            s = PacketParse.parse(s,preparedStatement,1,1);
            s = s.substring(s.indexOf("\n")+2);
            s = PacketParse.parse(s,preparedStatement,2,2);

            s = s.substring(s.indexOf("= (")+3);
            for(int i = 0; i < 2; i++){
                ind = s.indexOf(",");
                String l = s.substring(0,ind);
                preparedStatement.setString(4+i,l);
                s = s.substring(ind+2);
            }
            ind = s.indexOf(")");
            preparedStatement.setString(6,s.substring(0,ind));

            s = PacketParse.parse(s,preparedStatement,2,7);

            s = s.substring(s.indexOf(" (")+2);
            ind = s.indexOf(")");
            String type = s.substring(0,ind);
            preparedStatement.setString(9,type);
            s = s.substring(s.indexOf(": ")+2);

            s = PacketParse.parse(s,preparedStatement,2,10);
            preparedStatement.setString(12,Integer.toString(count));
            preparedStatement.setString(13,status);

            preparedStatement.execute();
            preparedStatement.close();

            if(type.equals("TCP")) {
                //saveToTCP(packet.getPayload());
            }
            if(type.equals("UDP")) {
                //saveToUDP(packet.getPayload());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveToUDP(Packet packet){

    }

    private static void saveToTCP(Packet packet){

    }

    private static void saveToARP(Packet packet){

    }

    private static void saveToDB(Packet packet,Timestamp timestamp){
        Connection connection = DatabaseConnection.setConnection();
        String packets = DataProperties.getProp("packets");
        String defaultstat = DataProperties.getProp("status.warning");

        if(count < COUNT) {
            count++;
        } else{
            count = 1;
        }

        try {
            String insert = "INSERT INTO "+packets+" VALUES(?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setString(1,timestamp.toString());
            preparedStatement.setString(2,defaultstat);

            String s = packet.getHeader().toString();
            s = PacketParse.parse(s,preparedStatement,2,3);

            s = s.substring(s.indexOf(" (")+2);
            int ind = s.indexOf(")");
            String type = s.substring(0,ind);

            preparedStatement.setString(5,type);
            preparedStatement.setString(6,Integer.toString(count));

            preparedStatement.execute();
            preparedStatement.close();

            if(type.equals("IPv4")) {
               saveToIPv4(packet.getPayload());
            }
            if(type.equals("IPv6")) {
               // saveToIPv6(packet.getPayload());
            }
            if(type.equals("ARP")){
                // saveToARP(packet.getPayload());
                int q = 0;
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
            System.out.close();
            System.in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        while (true) {
            try {
                Packet packet = handle.getNextPacketEx();
                System.out.println(packet);
                saveToDB(packet, handle.getTimestamp());
            } catch (TimeoutException e) {
                PacketCleaner.cleanDB();
            } catch (EOFException e) {
                e.printStackTrace();
                break;
            }
        }
        handle.close();
    }
}

