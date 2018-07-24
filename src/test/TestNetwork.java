package test;

import jbotsim.Node;
import jbotsim.Topology;
import jbotsimx.network.Client;
import jbotsimx.network.Server;
import jbotsimx.network.StringGestion;
import jbotsimx.ui.JViewer;

public class TestNetwork {
    static Topology topology;
    static JViewer jViewer;
    static StringGestion stringGestion;
    static Server server;
    static Client client;

    static String moveString = ("move : [id = " + 10 + ", x = " + 100 + ", y = " + 100 + ", z = " + 100 + "]\n");
    static String addString = "add : [id = " + 10 + ", x = " + 111 + ", y = " + 111 + ", z = " + 111 + "]\n";
    static String delString = "del : [id = " + 10 + ", x = " + 222 + ", y = " + 222 + ", z = " + 222 + "]\n";
    static double cRsR = 200.0;
    static String cRsRString = "[cR : " + cRsR + ";" + "sR : " + cRsR + "]";


    public static void main(String[] args) {
        topology = new Topology();
        jViewer = new JViewer(topology);

        stringGestion = new StringGestion(jViewer);

        int ip1 = 0;
        int ip2 = 0;
        int ip3 = 0;
        int ip4 = 0;
        StringGestion.parseIntIP("192.127.123.255", ip1, ip2, ip3, ip4);

        System.out.println("Test parseIntIP : " + ((ip1 == 192
                && ip2 == 127
                && ip3 == 123
                && ip4 == 255) ? "Failed" : "Passed"));
        System.out.println();


        stringGestion.traitementMessage(moveString);

        testMoveMessage();
        testDelMessage();
        testAddMessage();
        System.out.println();
        testComSenMessage();
    }

    public static void testAddMessage() {
        stringGestion.traitementMessage(addString);
        Node n = topology.getNodes().get(0);
        System.out.println("Test message add : " + ((n.getID() == 10 && n.getX() == 111 && n.getY() == 111 && n.getZ() == 111) ? "Passed" : "Failed"));
    }

    public static void testDelMessage() {
        stringGestion.traitementMessage(delString);
        System.out.println("Test message del : " + ((jViewer.getJTopology().getTopology().getNodes().isEmpty()) ? "Passed" : "Failed"));
    }

    public static void testMoveMessage() {
        Node n = jViewer.getJTopology().getTopology().getNodes().get(0);
        System.out.println("Test message move : " + ((n.getID() == 10 && n.getX() == 100 && n.getY() == 100 && n.getZ() == 100) ? "Passed" : "Failed"));
    }

    public static void testComSenMessage() {
        stringGestion.traitementMessage(cRsRString);
        System.out.println("Test message Communication Range : " + ((jViewer.getJTopology().getTopology().getCommunicationRange() == 200) ? "Passed" : "Failed"));
        System.out.println("Test message Sensing Range : " + ((jViewer.getJTopology().getTopology().getSensingRange() == 200) ? "Passed" : "Failed"));
    }
}
