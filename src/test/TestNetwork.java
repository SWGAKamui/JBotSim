package test;

import jbotsim.Node;
import jbotsim.Topology;
import jbotsimx.network.IP;
import jbotsimx.network.StringGestion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Timer;
import java.util.TimerTask;

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
    @DisplayName("Parsing IP String")
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
    @DisplayName("Add Node Message")
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
    @DisplayName("Delete Node Message")
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
    @DisplayName("Moving Node Message")
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
    @DisplayName("Communication Range | Sensing Range")
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
        TestServer server = new TestServer("127.0.0.1", 7776);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                server.server.close();
            }
        }, 100);

        server.run();
        assertTrue(server.server.isCreated());
    }

    @Test
    @DisplayName("Client is connected ?")
    public void testClient() {
        TestServer server = new TestServer("127.0.0.1", 7777);
        TestClient client = new TestClient("127.0.0.1", 7777);
        Timer t1 = new Timer();
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                client.run();
            }
        }, 100);

        Timer t2 = new Timer();
        t2.schedule(new TimerTask() {
            @Override
            public void run() {
                server.server.close();
            }
        }, 200);
        server.run();
        assertTrue(client.client.isCreated());
    }

    @Test
    @DisplayName("Send Nodes to Client")
    public void testSendNodes() {
        TestServer server = new TestServer("127.0.0.1", 7778);
        deployNodes(server.topology);
        TestClient client = new TestClient("127.0.0.1", 7778);
        Timer t1 = new Timer();
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                client.run();
            }
        }, 100);

        Timer t2 = new Timer();
        t2.schedule(new TimerTask() {
            @Override
            public void run() {
                server.server.close();
            }
        }, 200);
        server.run();
        assertTrue(client.client.isCreated());
        System.out.println(server.server.getTopology().getNodes().size() + "  " + client.client.getTopology().getNodes().size());
        assertEquals(server.server.getTopology().getNodes().size(), client.client.getTopology().getNodes().size());
    }

    @Test
    @DisplayName("Multi Client connection")
    public void testMultiClient() {
        TestServer server = new TestServer("127.0.0.1", 7779);
        TestClient client = new TestClient("127.0.0.1", 7779);
        TestClient client2 = new TestClient("127.0.0.1", 7779);
        TestClient client3 = new TestClient("127.0.0.1", 7779);
        Timer t1 = new Timer();
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                client.run();
            }
        }, 100);
        Timer t3 = new Timer();
        t3.schedule(new TimerTask() {
            @Override
            public void run() {
                client2.run();
            }
        }, 100);

        Timer t2 = new Timer();
        t2.schedule(new TimerTask() {
            @Override
            public void run() {
                client3.run();
            }
        }, 100);
        Timer t4 = new Timer();
        t4.schedule(new TimerTask() {
            @Override
            public void run() {
                server.server.close();
            }
        }, 500);

        server.run();

        assertEquals(3, server.server.getNbClientSave());
    }

    @Test
    @DisplayName("Multi Client Receive node")
    public void testMultiClientSendNode() {
        TestServer server = new TestServer("127.0.0.1", 7775);
        TestClient client = new TestClient("127.0.0.1", 7775);
        TestClient client2 = new TestClient("127.0.0.1", 7775);
        TestClient client3 = new TestClient("127.0.0.1", 7775);
        deployNodes(server.topology);
        Timer t1 = new Timer();
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                client.run();
            }
        }, 100);
        Timer t3 = new Timer();
        t3.schedule(new TimerTask() {
            @Override
            public void run() {
                client2.run();
            }
        }, 100);

        Timer t2 = new Timer();
        t2.schedule(new TimerTask() {
            @Override
            public void run() {
                client3.run();
            }
        }, 100);
        Timer t4 = new Timer();
        t4.schedule(new TimerTask() {
            @Override
            public void run() {
                server.server.close();
            }
        }, 500);

        server.run();
        assertAll("Multi Client receving",
                () -> assertEquals(server.server.getTopology().getNodes().size(),client.client.getTopology().getNodes().size()),
                () -> assertEquals(server.server.getTopology().getNodes().size(),client2.client.getTopology().getNodes().size()),
                () -> assertEquals(server.server.getTopology().getNodes().size(),client3.client.getTopology().getNodes().size())
        );
        System.out.println(server.server.getTopology().getNodes().size()+" "+client.client.getTopology().getNodes().size()
                +" "+client2.client.getTopology().getNodes().size()
                +" "+client3.client.getTopology().getNodes().size());
    }
}
