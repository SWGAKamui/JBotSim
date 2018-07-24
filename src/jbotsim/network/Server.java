package jbotsim.network;

import jbotsim.Color;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim._Properties;
import jbotsim.event.MovementListener;
import jbotsim.event.PropertyListener;
import jbotsim.event.TopologyListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Server implements MovementListener, TopologyListener, PropertyListener {
    private String messageToSend = "none";
    private String messageToSendSave = "none";
    private int nbClient = 0;
    private int nbClientSave = 0;
    private ArrayList<Integer> listIdToSend = new ArrayList<>();
    private List<Node> listNodestoAdd;
    private double comRange = 100;
    private double sensRange = 0;
    private Topology topology;

    private int ip1;
    private int ip2;
    private int ip3;
    private int ip4;

    public Server(Topology topology) {
        listNodestoAdd = topology.getNodes();
        this.topology = topology;
    }

    public void parseIntIP(String serverIp) {
        ip1 = Integer.parseInt(serverIp.substring(0, serverIp.indexOf(".")));
        serverIp = serverIp.substring(serverIp.indexOf(".") + 1, serverIp.length());

        ip2 = Integer.parseInt(serverIp.substring(0, serverIp.indexOf(".")));
        serverIp = serverIp.substring(serverIp.indexOf(".") + 1, serverIp.length());

        ip3 = Integer.parseInt(serverIp.substring(0, serverIp.indexOf(".")));
        serverIp = serverIp.substring(serverIp.indexOf(".") + 1, serverIp.length());

        ip4 = Integer.parseInt(serverIp);
    }

    public void run(String serverIp) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            parseIntIP(serverIp);

            byte[] address = {(byte) ip1, (byte) ip2, (byte) ip3, (byte) ip4};
            InetAddress ip = InetAddress.getByAddress(address);

            serverSocketChannel.bind(new InetSocketAddress(ip, 7777));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server is created");

            while (true) {
                selector.select();
                Set<SelectionKey> Keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = Keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey myKey = iterator.next();
                    if (myKey.isAcceptable()) {
                        SocketChannel client = serverSocketChannel.accept();
                        nbClient++;
                        client.configureBlocking(false);

                        client.register(selector, SelectionKey.OP_WRITE);
                    } else if (myKey.isWritable()) {
                        byte[] message;
                        ByteBuffer buffer;
                        SocketChannel client = (SocketChannel) myKey.channel();

                        if (comRange != topology.getCommunicationRange() || sensRange != topology.getSensingRange()) {
                            messageToSendSave = ("[cR : " + topology.getCommunicationRange() + ";" + "sR : " + topology.getSensingRange() + "]");
                            comRange = topology.getCommunicationRange();
                            sensRange = topology.getSensingRange();

                        }
                        if (!listNodestoAdd.isEmpty()) {
                            messageToSendSave = ("add : [id = " + listNodestoAdd.get(0).getID()
                                    + " , x = " + listNodestoAdd.get(0).getX()
                                    + " , y = " + listNodestoAdd.get(0).getY()
                                    + " , z = " + listNodestoAdd.get(0).getZ() + "]\n");
                            listNodestoAdd.remove(0);
                            if (!listNodestoAdd.isEmpty()) {
                                messageToSendSave += ("add : [id = " + listNodestoAdd.get(0).getID()
                                        + " , x = " + listNodestoAdd.get(0).getX()
                                        + " , y = " + listNodestoAdd.get(0).getY()
                                        + " , z = " + listNodestoAdd.get(0).getZ() + "]\n");
                                listNodestoAdd.remove(0);
                            }
                        }
                        if (!messageToSendSave.contains("none")) {
                            message = messageToSendSave.getBytes();
                            buffer = ByteBuffer.wrap(message);
                            client.write(buffer);
                            messageToSendSave = "none";
                        } else {
                            message = messageToSend.getBytes();
                            buffer = ByteBuffer.wrap(message);
                            client.write(buffer);
                            if (messageToSend.contains("move"))
                                listIdToSend.clear();
                            messageToSend = "none";
                        }
                    }
                    Thread.sleep(30);
                    iterator.remove();
                }
                if (nbClientSave != nbClient) {
                    nbClientSave = nbClient;
                    System.out.println("Number of person connected : " + nbClient);
                }
            }
        } catch (IOException | InterruptedException e) {
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
}
