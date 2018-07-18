package jbotsim.network;

import examples.basic.moving.MovingNode;
import jbotsim.Topology;
import jbotsim.ui.JViewer;

public class ServerMain {
    public static void main(String[] args) {
        Topology topology = new Topology();
        topology.setDefaultNodeModel(MovingNode.class);
        topology.addNode(100,100);
        topology.addNode(100,100);
        topology.addNode(100,100);
        topology.addNode(100,100);
        topology.addNode(100,100);
        topology.addNode(100,100);
        topology.addNode(100,100);
        topology.addNode(100,100);
        topology.addNode(100,100);
        topology.addNode(100,100);
        topology.addNode(100,100);
        topology.start();

        JViewer jViewer = new JViewer(topology);
        jViewer.setTitle("Server");

        Server server = new Server(jViewer);
        server.run();
    }

}
