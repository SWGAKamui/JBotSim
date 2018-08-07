package test;

import jbotsim.Topology;
import jbotsimx.network.ClientUDP;

public class TestClient {
    private final int port;
    private final String ip;
    Topology topology;
    ClientUDP client;

    private static void deployNodes(Topology tp) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                tp.addNode(50 + i * 80, 50 + j * 80);
            }
        }
    }
    public TestClient(String ip, int port){
        this.ip = ip;
        this.port = port;
    }


    public void run() {
        this.topology = new Topology();
        deployNodes(topology);
        client = new ClientUDP(topology);
        client.run(ip, port);
    }
}
