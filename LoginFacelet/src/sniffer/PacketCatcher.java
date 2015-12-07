package sniffer;

import dataclasses.connections.DataProperties;
import dataclasses.packets.PacketParse;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
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

    private static void saveToIPv4(IpV4Packet packet){
        Connection connection = DatabaseConnection.setConnection();
        String ipv4 = DataProperties.getProp("ipv4");
        String status = DataProperties.getProp("status.warning");

        IpV4Packet.IpV4Header header = packet.getHeader();


        try {
            String insert = "INSERT INTO "+ipv4+" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setString(1,header.getVersion().toString());
            preparedStatement.setString(2,Integer.toString(header.getIhl()));
            preparedStatement.setString(3,"21");
            preparedStatement.setString(4,Integer.toString(header.getTotalLengthAsInt()));
            preparedStatement.setString(5,Integer.toString(header.getIdentificationAsInt()));
            preparedStatement.setString(6,Boolean.toString(header.getReservedFlag()));
            preparedStatement.setString(7,Boolean.toString(header.getDontFragmentFlag()));
            preparedStatement.setString(8,Boolean.toString(header.getMoreFragmentFlag()));
            preparedStatement.setString(9,Integer.toString(header.getFragmentOffset()));
            preparedStatement.setString(10,Integer.toString(header.getTtl()));
            preparedStatement.setString(11,header.getProtocol().toString());
            preparedStatement.setString(12,Integer.toString(header.getHeaderChecksum()));
            preparedStatement.setString(13,header.getSrcAddr().toString());
            preparedStatement.setString(14,header.getDstAddr().toString());
            preparedStatement.setString(15,header.getOptions().toString());
            preparedStatement.setString(16,Integer.toString(count));
            preparedStatement.setString(17,status);

            preparedStatement.execute();
            preparedStatement.close();

            Packet next = packet.getPayload();
            Class clazz = next.getClass();
            if(clazz.equals(TcpPacket.class)){
                //saveToTCP(packet.getPayload());
            }
            if(clazz.equals(UdpPacket.class)){
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

    private static void saveToDB(EthernetPacket packet, Timestamp timestamp){
        Connection connection = DatabaseConnection.setConnection();
        String packets = DataProperties.getProp("packets");
        String defaultstat = DataProperties.getProp("status.warning");

        EthernetPacket.EthernetHeader header = packet.getHeader();
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
            preparedStatement.setString(3,header.getDstAddr().toString());
            preparedStatement.setString(4,header.getSrcAddr().toString());

            String s = header.getType().toString();
            s = s.substring(s.indexOf("(")+1,s.indexOf(")"));

            preparedStatement.setString(5,s);
            preparedStatement.setString(6,Integer.toString(count));

            preparedStatement.execute();
            preparedStatement.close();

            Packet next = packet.getPayload();
            Class clazz = next.getClass();
            if(clazz.equals(IpV4Packet.class)) {
               saveToIPv4(next.get(IpV4Packet.class));
            }
            if(clazz.equals((IpV6Packet.class))) {
               // saveToIPv6(packet.getPayload());
            }
            if(clazz.equals(ArpPacket.class)){
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
                //System.out.println(packet);
                saveToDB(packet.get(EthernetPacket.class), handle.getTimestamp());
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

