package jbotsimx.network;

import jbotsim.Topology;
import jbotsimx.ui.JViewer;


public class ServerMain {
    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("USAGE : Server IP     Port");
            System.exit(0);
        }
        System.out.println("Server IP : "+args[0]);
        Topology topology = new Topology();

        JViewer jViewer = new JViewer(topology);
        jViewer.setTitle("Server");
        topology.start();

        Server server = new Server(topology);
        jViewer.getJTopology().getTopology().addMovementListener(server);
        jViewer.getJTopology().getTopology().addTopologyListener(server);
        server.run(args[0], Integer.parseInt(args[1]));
    }


}