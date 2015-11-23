package sniffer;

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
import java.util.concurrent.TimeoutException;

public class PacketCatcher{

    private static final String COUNT_KEY
            = GetNextPacketEx.class.getName() + ".count";
    private static final int COUNT
            = Integer.getInteger(COUNT_KEY, 5);

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

    private void claenDB(){

    }

    private void demonize() throws IOException {
        System.out.close();
        System.in.close();
    }

    private void saveToDB(Packet packet, Timestamp timestamp){
        Connection connection = DatabaseConnection.setConnection();
        try {
            String insert = "INSERT INTO packets VALUES(?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1,timestamp.toString());
            String s = packet.getHeader().toString();
            preparedStatement.setString(2,s);
            preparedStatement.setString(2,"0");

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void catchPacket(String argv[]) throws PcapNativeException, NotOpenException {
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

        try {
            demonize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        isWork = true;
        while (isWork) {
            try {
                Packet packet = handle.getNextPacketEx();
                saveToDB(packet, handle.getTimestamp() );
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (EOFException e) {
                e.printStackTrace();
            }
        }
        handle.close();
    }
}

