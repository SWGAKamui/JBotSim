package jbotsimx.network;

import jbotsim.Color;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim._Properties;
import jbotsim.event.MovementListener;
import jbotsim.event.PropertyListener;
import jbotsim.event.TopologyListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Server implements MovementListener, TopologyListener, PropertyListener {
    ServerSocketChannel serverSocketChannel;
    private Topology topology;
    private String messageToSend = "none";
    private String messageToSendSave = "none";
    private int nbClient = 0;
    private int nbClientSave = 0;
    private ArrayList<Integer> listIdToSend = new ArrayList<>();
    private List<Node> listNodestoAdd;
    private double comRange = 100;
    private double sensRange = 0;
    private Boolean exception = false;
    private Boolean hasCreatedServer = false;
    private IP ip = new IP();
    private SocketChannel client;
    private StringGestion stringGestion;

    public Server(Topology topology) {
        stringGestion = new StringGestion(topology);
        listNodestoAdd = topology.getNodes();
        this.topology = topology;
    }
    public int getNbClientSave(){
        return nbClientSave;
    }

    public void run(String serverIp, int port) {
        try {
            System.out.println("Server try to connect ****");

            StringGestion.parseIntIP(serverIp, ip);
            byte[] address = {(byte) ip.ip1, (byte) ip.ip2, (byte) ip.ip3, (byte) ip.ip4};
            InetAddress ip = InetAddress.getByAddress(address);

            client = SocketChannel.open(new InetSocketAddress(ip, port));
            client.socket().setTcpNoDelay(true);

            System.out.println("Waiting for connection ***");
            while (!client.finishConnect()) {
                //wait connection
                System.out.print("*");
            }

            System.out.println("server is connected : " + client.isConnected() + "\n");

            while (client.isConnected()) {
                if(!messageToSendSave.equals("none"))
                    client.write(ByteBuffer.wrap(messageToSendSave.getBytes()));
                else
                    client.write(ByteBuffer.wrap(messageToSendSave.getBytes()));
            }
            client.close();
            System.out.println("server is closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNodeAdded(Node node) {
        if (messageToSend.contains("del") || messageToSend.contains("add"))
            messageToSendSave = messageToSend;
        if (nbClient > 0) {
            messageToSend = ("add : [id = " + node.getID() + " , x = " + node.getX() + " , y = " + node.getY() + " , z = " + node.getZ() + "]");
        }
    }

    @Override
    public void onNodeRemoved(Node node) {
        if (messageToSend.contains("del") || messageToSend.contains("add"))
            messageToSendSave = messageToSend;
        if (nbClient > 0) {
            messageToSend = ("del : [id = " + node.getID() + " " + " , x = " + node.getX() + " , y = " + node.getY() + " , z = " + node.getZ() + "]");
        }
    }

    @Override
    public void onMove(Node node) {
        if (nbClient > 0) {
            if (messageToSend.contains("del") || messageToSend.contains("add"))
                messageToSendSave = messageToSend;
            if (!listIdToSend.contains(node.getID())) {
                if (!messageToSend.equals("none") && !messageToSend.contains("add")
                        && !messageToSend.contains("del") && !messageToSend.contains("sR")
                        && !messageToSend.contains("cR")) {
                    messageToSend += ("move : [id = " + node.getID() + ", x = " + node.getX() + ", y = " + node.getY() + ", z = " + node.getZ() + "]\n");
                } else {
                    messageToSend = ("move : [id = " + node.getID() + ", x = " + node.getX() + ", y = " + node.getY() + ", z = " + node.getZ() + "]\n");
                }
            }
            listIdToSend.add(node.getID());
        }
    }

    @Override
    public void propertyChanged(_Properties o, String property) {
        if (o instanceof Node) { // Node
            Node node = new Node();
            node.getProperty("jnode");
            switch (property) {
                case "color":
                    messageToSend = ("color : [id = " + node.getID() + "," + node.getIntColor() + "]\n");
                    System.out.println(node.getIntColor());
                    break;
                case "icon":
                    // messageToSend = ("color : [id = " + node.getID() + ","+node.+"]\n");
                    break;
                case "size":
                    messageToSend = ("size : [id = " + node.getID() + "," + node.getSize() + "]\n");
                    break;
            }
        } else {
            messageToSend = ("color : [id = " + 0 + "," + Color.red.toString() + "]\n");
            System.out.println("ici +" + Color.red.toString());
        }
    }

    public Topology getTopology() {
        return topology;
    }

    public boolean isCreated() {
        return hasCreatedServer;
    }

    public void close() {
        exception = true;
        System.out.println("server is closed");
    }
}
