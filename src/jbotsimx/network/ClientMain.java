package jbotsimx.network;

import jbotsim.Topology;
import jbotsimx.ui.JViewer;

import java.util.Timer;
import java.util.TimerTask;

public class ClientMain {
    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("USAGE : Server IP, port");
            System.exit(0);
        }
        Topology topology = new Topology();
        JViewer jViewer = new JViewer(topology);

        jViewer.setTitle("Client");
        ClientTCP clientTCP = new ClientTCP(topology);
        ClientUDP clientUDP = new ClientUDP(topology);


        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                clientUDP.run(args[0], Integer.parseInt(args[1])+1);
            }
        }, 200);
        clientTCP.run(args[0], Integer.parseInt(args[1]));
    }

}
