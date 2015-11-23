package sniffer;

import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.io.IOException;
import java.util.List;

public class NIFSelector{
    public static PcapNetworkInterface selectNif() throws IOException {
        List<PcapNetworkInterface> allDevs = null;
        try {
            allDevs = Pcaps.findAllDevs();
        } catch (PcapNativeException e) {
            throw new IOException(e.getMessage());
        }

        if (allDevs == null || allDevs.size() == 0) {
            throw new IOException("No NIF to capture.");
        }

        return allDevs.get(0);
    }
}
