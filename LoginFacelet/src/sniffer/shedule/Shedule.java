package sniffer.shedule;

import dataclasses.connections.DataProperties;

import java.util.Timer;
import java.util.TimerTask;

public class Shedule {
    public static void main(String argv[]){
        Timer t = new Timer();
        TimerTask tt = new MemTask();

        int time = DataProperties.getPropInt("time");
        int tick = DataProperties.getPropInt("time.tick");
        int repeate = DataProperties.getPropInt("time.repeate");

        t.scheduleAtFixedRate(tt, repeate, time*tick);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.cancel();
    }
}
