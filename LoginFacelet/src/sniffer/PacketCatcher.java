package sniffer;

import dataclasses.connections.DataProperties;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.sample.GetNextPacketEx;
import dataclasses.connections.DatabaseConnection;
import sniffer.shedule.PacketCleaner;

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

    private static void saveToIPv6(IpV6Packet packet, int cnt){
        Connection connection = DatabaseConnection.setConnection();
        String ipv6 = DataProperties.getProp("ipv6");
        String status = DataProperties.getProp("status.warning");

        IpV6Packet.IpV6Header header = packet.getHeader();

        try {
            String insert = "INSERT INTO "+ipv6+" VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setString(1,header.getTrafficClass().toString());
            preparedStatement.setString(2,header.getFlowLabel().toString());
            preparedStatement.setInt(3,header.getPayloadLengthAsInt());
            preparedStatement.setString(4, header.getNextHeader().toString());
            preparedStatement.setInt(5,header.getHopLimit());
            preparedStatement.setString(6,header.getSrcAddr().toString());
            preparedStatement.setString(7,header.getDstAddr().toString());
            preparedStatement.setInt(8,cnt);
            preparedStatement.setString(9,status);

            preparedStatement.execute();
            preparedStatement.close();

            Packet next = packet.getPayload();
            Class clazz = next.getClass();
            if(clazz.equals(TcpPacket.class)){
                saveToTCP(next.get(TcpPacket.class),cnt);
            }
            if(clazz.equals(UdpPacket.class)){
                saveToUDP(next.get(UdpPacket.class),cnt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveToIPv4(IpV4Packet packet,int cnt){
        Connection connection = DatabaseConnection.setConnection();
        String ipv4 = DataProperties.getProp("ipv4");
        String status = DataProperties.getProp("status.warning");

        IpV4Packet.IpV4Header header = packet.getHeader();


        try {
            String insert = "INSERT INTO "+ipv4+" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setInt(1,header.getIhl());
            preparedStatement.setInt(2,header.getTotalLengthAsInt());
            preparedStatement.setInt(3,header.getIdentificationAsInt());
            preparedStatement.setBoolean(4, header.getReservedFlag());
            preparedStatement.setBoolean(5,header.getDontFragmentFlag());
            preparedStatement.setBoolean(6,header.getMoreFragmentFlag());
            preparedStatement.setInt(7,header.getFragmentOffset());
            preparedStatement.setInt(8,header.getTtlAsInt());

            String s = header.getProtocol().toString();
            s = s.substring(s.indexOf("(")+1,s.indexOf(")"));

            preparedStatement.setString(9,s);
            preparedStatement.setInt(10,header.getHeaderChecksum());
            preparedStatement.setString(11,header.getSrcAddr().toString());
            preparedStatement.setString(12,header.getDstAddr().toString());
            preparedStatement.setInt(13,cnt);
            preparedStatement.setString(14,status);

            preparedStatement.execute();
            preparedStatement.close();

            Packet next = packet.getPayload();
            Class clazz = next.getClass();
            if(clazz.equals(TcpPacket.class)){
                saveToTCP(next.get(TcpPacket.class), cnt);
            }
            if(clazz.equals(UdpPacket.class)){
                saveToUDP(next.get(UdpPacket.class), cnt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveToUDP(UdpPacket packet, int cnt){
        Connection connection = DatabaseConnection.setConnection();
        String udp = DataProperties.getProp("udp");
        String status = DataProperties.getProp("status.warning");

        UdpPacket.UdpHeader header = packet.getHeader();

        try {
            String insert = "INSERT INTO "+udp+" VALUES(?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setString(1,header.getSrcPort().valueAsString());
            preparedStatement.setString(2,header.getDstPort().valueAsString());
            preparedStatement.setInt(3,header.getLength());
            preparedStatement.setInt(4, header.getChecksum());
            preparedStatement.setInt(5,cnt);
            preparedStatement.setString(6,status);

            preparedStatement.execute();
            preparedStatement.close();

            Packet next = packet.getPayload();
            if(next != null){
                saveToData(next.get(UnknownPacket.class), cnt);
            }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
        }
    }

    private static void saveToTCP(TcpPacket packet, int cnt){
        Connection connection = DatabaseConnection.setConnection();
        String tcp = DataProperties.getProp("tcp");
        String status = DataProperties.getProp("status.warning");

        TcpPacket.TcpHeader header = packet.getHeader();

        try {
            String insert = "INSERT INTO "+tcp+" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setString(1,header.getSrcPort().valueAsString());
            preparedStatement.setString(2,header.getDstPort().valueAsString());
            preparedStatement.setString(3,Long.toString(header.getSequenceNumberAsLong()));
            preparedStatement.setString(4, Long.toString(header.getAcknowledgmentNumberAsLong()));
            preparedStatement.setInt(5,header.getDataOffset());
            preparedStatement.setInt(6,header.getReserved());
            preparedStatement.setBoolean(7,header.getUrg());
            preparedStatement.setBoolean(8,header.getAck());
            preparedStatement.setBoolean(9,header.getPsh());
            preparedStatement.setBoolean(10,header.getRst());
            preparedStatement.setBoolean(11,header.getSyn());
            preparedStatement.setBoolean(12,header.getFin());
            preparedStatement.setInt(13, header.getWindow());
            preparedStatement.setInt(14,header.getChecksum());
            preparedStatement.setInt(15,header.getUrgentPointer());
            preparedStatement.setBytes(16, header.getPadding());
            preparedStatement.setInt(17,cnt);
            preparedStatement.setString(18,status);

            preparedStatement.execute();
            preparedStatement.close();

            Packet next = packet.getPayload();
            if(next != null) {
                saveToData(next.get(UnknownPacket.class), cnt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private static void saveToARP(ArpPacket packet, int cnt){
        Connection connection = DatabaseConnection.setConnection();
        String arp = DataProperties.getProp("arp");
        String status = DataProperties.getProp("status.warning");

        ArpPacket.ArpHeader header = packet.getHeader();

        try {
            String insert = "INSERT INTO "+arp+" VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setInt(1,header.getHardwareType().value());
            preparedStatement.setInt(2,header.getProtocolType().value());
            preparedStatement.setInt(3,header.getHardwareAddrLength());
            preparedStatement.setInt(4, header.getProtocolAddrLength());
            preparedStatement.setString(5,header.getOperation().toString());
            preparedStatement.setString(6,header.getSrcHardwareAddr().toString());
            preparedStatement.setString(7,header.getSrcProtocolAddr().toString());
            preparedStatement.setString(8,header.getDstHardwareAddr().toString());
            preparedStatement.setString(9,header.getDstProtocolAddr().toString());
            preparedStatement.setInt(10,cnt);
            preparedStatement.setString(11,status);

            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveToData(UnknownPacket packet, int cnt){
        Connection connection = DatabaseConnection.setConnection();
        String data = DataProperties.getProp("data");
        String status = DataProperties.getProp("status.warning");

        try {
            String insert = "INSERT INTO "+data+" VALUES(?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            int s = packet.toHexString().length();
            preparedStatement.setString(1,packet.toHexString());
            preparedStatement.setInt(2,cnt);
            preparedStatement.setString(3,status);

            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        int cnt = count;

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
            preparedStatement.setInt(6,cnt);

            preparedStatement.execute();
            preparedStatement.close();

            Packet next = packet.getPayload();
            Class clazz = next.getClass();
            if(clazz.equals(IpV4Packet.class)) {
               saveToIPv4(next.get(IpV4Packet.class),cnt);
            }
            if(clazz.equals((IpV6Packet.class))) {
               saveToIPv6(next.get(IpV6Packet.class),cnt);
            }
            if(clazz.equals(ArpPacket.class)){
                saveToARP(packet.get(ArpPacket.class), cnt);
            }
            if(clazz.toString().contains("Icmp")){
                int g = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) throws PcapNativeException, NotOpenException {
        String filter = "";
        PcapNetworkInterface nif;

        try {
            nif =  PacketCleaner.NIFSelector.selectNif();
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

        PacketCleaner.cleanDB();
        while (true) {
            try {
                Packet packet = handle.getNextPacketEx();
                saveToDB(packet.get(EthernetPacket.class), handle.getTimestamp());
            } catch (TimeoutException e) {
                //PacketCleaner.cleanDB();
            } catch (EOFException e) {
                break;
            }
        }
        handle.close();
    }
}

