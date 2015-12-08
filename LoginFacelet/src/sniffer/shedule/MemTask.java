package sniffer.shedule;

import java.util.TimerTask;

public class MemTask extends TimerTask {

    public MemTask(){}

    @Override
    public void run() {
        PacketStatistic.getStatistic();
        PacketCleaner.cleanDB();
    }

}
