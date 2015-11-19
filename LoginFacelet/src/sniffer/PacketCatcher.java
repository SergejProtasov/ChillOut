package sniffer;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.sample.GetNextPacketEx;
import org.pcap4j.util.NifSelector;

import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PacketCatcher {

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

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean work) {
        isWork = work;
    }

    public void catchPacket() throws PcapNativeException, NotOpenException {
        String filter = "";

        PcapNetworkInterface nif;

        try {
            nif = new NifSelector().selectNetworkInterface();
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
        isWork = true;

        while (isWork) {
            try {
                Packet packet = handle.getNextPacketEx();
                System.out.println(handle.getTimestamp());
                System.out.println(packet);
            } catch (TimeoutException e) {
            } catch (EOFException e) {
                e.printStackTrace();
            }
        }

        handle.close();
    }
}

