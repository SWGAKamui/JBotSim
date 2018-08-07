package jbotsimx.network;

import examples.basic.moving.MovingNode;
import jbotsim.Topology;
import jbotsimx.ui.JViewer;

import java.util.Timer;
import java.util.TimerTask;


public class ServerMain {
    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("USAGE : Server IP     Port");
            System.exit(0);
        }
        System.out.println("Server IP : "+args[0]);
        Topology topology = new Topology();
        topology.setDefaultNodeModel(MovingNode.class);
        JViewer jViewer = new JViewer(topology);
        jViewer.setTitle("Server");
        topology.start();

        ServerTCP serverTCP = new ServerTCP(topology);
        jViewer.getJTopology().getTopology().addTopologyListener(serverTCP);

        ServerUDP serverUDP = new ServerUDP(topology);
        jViewer.getJTopology().getTopology().addMovementListener(serverUDP);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                serverUDP.run(args[0], Integer.parseInt(args[1])+1);
            }
        }, 200);
        serverTCP.run(args[0], Integer.parseInt(args[1]));

    }
}