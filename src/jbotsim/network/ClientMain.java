package jbotsim.network;

import jbotsim.Topology;
import jbotsim.ui.JViewer;

public class ClientMain {
    public static void main(String[] args) {
        Topology topology = new Topology();
        JViewer jViewer = new JViewer(topology);

        jViewer.setTitle("Client");
        Client client = new Client(jViewer);

        client.run();
    }

}
