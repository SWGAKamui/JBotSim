package jbotsim.network;

import examples.basic.moving.MovingNode;
import jbotsim.Topology;
import jbotsim.ui.JViewer;

public class ServerMain {
    public static void main(String[] args) {
        Topology topology = new Topology();
        //topology.setDefaultNodeModel(BroadcastingNode.class);
        topology.setDefaultNodeModel(MovingNode.class);

        deployNodes(topology);

        JViewer jViewer = new JViewer(topology);
        jViewer.setTitle("Server");
        topology.start();

        Server server = new Server(topology);
        jViewer.getJTopology().getTopology().addMovementListener(server);
        jViewer.getJTopology().getTopology().addTopologyListener(server);
        server.run();
    }

    private static void deployNodes(Topology tp) {
        for (int i = 0; i < 7; i++){
            for (int j = 0; j < 5; j++) {
                tp.addNode(50 + i * 80, 50 + j * 80);
            }
        }
    }

}