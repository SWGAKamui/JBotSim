package jbotsimx.network;

import jbotsim.Topology;
import jbotsimx.ui.JViewer;

public class ClientMain {
    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("USAGE : Server IP, port");
            System.exit(0);
        }
        Topology topology = new Topology();
        JViewer jViewer = new JViewer(topology);

        jViewer.setTitle("Client");
        Client client = new Client(topology);

        client.run(args[0], Integer.parseInt(args[1]));
    }

}
