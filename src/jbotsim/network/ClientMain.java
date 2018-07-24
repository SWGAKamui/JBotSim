package jbotsim.network;

import jbotsim.Topology;
import jbotsimx.ui.JViewer;

public class ClientMain {
    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("USAGE : Server IP");
            System.exit(0);
        }
        Topology topology = new Topology();
        JViewer jViewer = new JViewer(topology);

        jViewer.setTitle("Client");
        Client client = new Client(jViewer);

        client.run(args[0]);
    }

}
