package test;

import jbotsim.Topology;
import jbotsimx.network.Server;

public class TestServer {
    private final int port;
    private final String ip;
    public Topology topology = new Topology();
    Server server;

    public TestServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }


    public void run() {
        server = new Server(topology);
        server.run(ip, port);
    }
}
