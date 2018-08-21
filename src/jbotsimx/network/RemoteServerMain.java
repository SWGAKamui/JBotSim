package jbotsimx.network;

import jbotsim.Topology;
import java.util.Timer;
import java.util.TimerTask;

public class RemoteServerMain {
    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("USAGE : Server IP     Port");
            System.exit(0);
        }
        System.out.println("Server IP : "+args[0]);
        Topology topology = new Topology();

        topology.start();

        RemoteServer server = new RemoteServer(topology);
        RemoteServerClientSide remoteServerClientSide = new RemoteServerClientSide(topology);
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                remoteServerClientSide.run(args[0], Integer.parseInt(args[1])+1);
            }
        },100);

        server.run(args[0], Integer.parseInt(args[1]));
    }
}
