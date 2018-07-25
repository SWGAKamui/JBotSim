package test;

import examples.basic.moving.MovingNode;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsimx.network.Client;
import jbotsimx.network.IP;
import jbotsimx.network.Server;
import jbotsimx.network.StringGestion;
import jbotsimx.ui.JViewer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestNetwork {
    private static void deployNodes(Topology tp) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                tp.addNode(50 + i * 80, 50 + j * 80);
            }
        }
    }

    @Test
    public void testIP() {
        IP ip = new IP();
        StringGestion.parseIntIP("192.127.123.255", ip);
        assertAll("Add Message",
                () -> assertEquals(192, ip.ip1),
                () -> assertEquals(127, ip.ip2),
                () -> assertEquals(123, ip.ip3),
                () -> assertEquals(255, ip.ip4)
        );
    }

    @Test
    public void testAddMessage() {
        String addString = "add : [id = " + 10 + ", x = " + 111 + ", y = " + 111 + ", z = " + 111 + "]\n";
        Topology topology = new Topology();
        StringGestion stringGestion = new StringGestion(topology);
        stringGestion.traitementMessage(addString);
        Node n = topology.getNodes().get(0);
        assertAll("Add Message",
                () -> assertEquals(10, n.getID()),
                () -> assertEquals(111, n.getX()),
                () -> assertEquals(111, n.getY()),
                () -> assertEquals(111, n.getZ())
        );
    }

    @Test
    public void testDelMessage() {
        String delString = "del : [id = " + 10 + ", x = " + 222 + ", y = " + 222 + ", z = " + 222 + "]\n";
        Topology topology = new Topology();
        Node n = new Node();
        n.setLocation(100, 100);
        n.setID(10);
        topology.addNode(n);
        StringGestion stringGestion = new StringGestion(topology);

        stringGestion.traitementMessage(delString);
        assertTrue(topology.getNodes().isEmpty());
    }

    @Test
    public void testMoveMessage() {
        String moveString = ("move : [id = " + 10 + ", x = " + 100 + ", y = " + 100 + ", z = " + 100 + "]\n");
        Topology topology = new Topology();
        StringGestion stringGestion = new StringGestion(topology);
        stringGestion.traitementMessage(moveString);
        Node n = topology.getNodes().get(0);
        assertAll("Move message",
                () -> assertEquals(10, n.getID()),
                () -> assertEquals(100, n.getX()),
                () -> assertEquals(100, n.getY()),
                () -> assertEquals(100, n.getZ())
        );
        moveString = ("move : [id = " + 10 + ", x = " + 110 + ", y = " + 110 + ", z = " + 110 + "]\n");
        stringGestion.traitementMessage(moveString);
        assertAll("Move message",
                () -> assertEquals(10, n.getID()),
                () -> assertEquals(110, n.getX()),
                () -> assertEquals(110, n.getY()),
                () -> assertEquals(110, n.getZ())
        );
    }

    @Test
    public void testComSenMessage() {
        double cRsR = 200.0;
        String cRsRString = "[cR : " + cRsR + ";" + "sR : " + cRsR + "]";
        Topology topology = new Topology();
        StringGestion stringGestion = new StringGestion(topology);
        stringGestion.traitementMessage(cRsRString);
        assertAll("Comm | Sensing Range",
                () -> assertEquals(cRsR, topology.getCommunicationRange()),
                () -> assertEquals(cRsR, topology.getSensingRange())
        );
    }

    @Test
    @DisplayName("Server is connected ?")
    public void testServer() {
        Server server = new Server(new Topology());
        server.setTest();
        server.run("127.0.0.1");
       // assertTrue(server.isCreated());
    }

    //@Test
    @DisplayName("Client is connected ?")
    public void testClient() {
        Client client = new Client(new JViewer(new Topology()));
        client.setTest();
        assertTrue(client.createClient("127.0.0.1"));
    }
}
