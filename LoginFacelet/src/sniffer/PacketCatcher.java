package sniffer;

import dataclasses.DataProperties;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.sample.GetNextPacketEx;
import dataclasses.DatabaseConnection;

import java.io.EOFException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;

public class PacketCatcher{

    private static final String COUNT_KEY
            = GetNextPacketEx.class.getName() + ".count";

    private static final String READ_TIMEOUT_KEY
            = GetNextPacketEx.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT
            = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]

    private static final String SNAPLEN_KEY
            = GetNextPacketEx.class.getName() + ".snaplen";
    private static final int SNAPLEN
            = Integer.getInteger(SNAPLEN_KEY, 65536);

    private boolean isWork = false;
    private boolean wait = false;

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean work) {
        isWork = work;
    }

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

    private void demonize() throws IOException {
        System.out.close();
        System.in.close();
    }

    private static void saveToDB(Packet packet, Timestamp timestamp){
        Connection connection = DatabaseConnection.setConnection();
        String packets = DataProperties.getProp("packets");

        try {
            String insert = "INSERT INTO "+packets+" VALUES(?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);

            preparedStatement.setString(1,timestamp.toString());
            String s = packet.getHeader().toString();
            preparedStatement.setString(2,s);
            preparedStatement.setString(3,"0");

            preparedStatement.execute();
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

        //isWork = true;
        while (true) {
            try {
                Packet packet = handle.getNextPacketEx();
                saveToDB(packet, handle.getTimestamp() );
            } catch (TimeoutException e) {
                e.printStackTrace();
                cleanDB();
            } catch (EOFException e) {
                e.printStackTrace();
                break;
            }
        }
        handle.close();
    }
}

