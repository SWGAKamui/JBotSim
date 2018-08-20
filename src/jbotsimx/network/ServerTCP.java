package jbotsimx.network;

import jbotsim.Color;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim._Properties;

import jbotsim.event.PropertyListener;
import jbotsim.event.TopologyListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ServerTCP implements TopologyListener, PropertyListener {
    ServerSocketChannel serverSocketChannel;
    private Topology topology;
    private String messageToSend = "none";
    private int nbClient = 0;
    private int nbClientSave = 0;
    private List<Node> listNodestoAdd;
    private double comRange = 100;
    private double sensRange = 0;
    private Boolean exception = false;
    private Boolean hasCreatedServer = false;

    public ServerTCP(Topology topology) {
        listNodestoAdd = topology.getNodes();
        this.topology = topology;
    }

    public int getNbClientSave() {
        return nbClientSave;
    }

    public void run(String serverIp, int port) {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().setReuseAddress(true);

            IP ip = new IP();
            StringGestion.parseIntIP(serverIp, ip);

            byte[] address = {(byte) ip.ip1, (byte) ip.ip2, (byte) ip.ip3, (byte) ip.ip4};
            InetAddress ipAdd = InetAddress.getByAddress(address);

            serverSocketChannel.bind(new InetSocketAddress(ipAdd, port));

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server is created");

            while (!exception) {
                hasCreatedServer = true;
                selector.selectNow();

                Set<SelectionKey> Keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = Keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey myKey = iterator.next();
                    addComSens();
                    if (myKey.isAcceptable()) {
                        SocketChannel client = serverSocketChannel.accept();
                        nbClient++;
                        client.configureBlocking(false);

                        client.register(selector, SelectionKey.OP_WRITE);
                        System.out.println("ici");
                    } else if (myKey.isWritable()) {
                        byte[] message;
                        ByteBuffer buffer;
                        SocketChannel client = (SocketChannel) myKey.channel();

                        addNodes();


                        if (!messageToSend.contains("none")) {
                            message = messageToSend.getBytes();
                            buffer = ByteBuffer.wrap(message);
                            client.write(buffer);
                        }
                    }

                    if (myKey.isReadable()) {
                        SocketChannel client = serverSocketChannel.accept();
                        nbClient++;
                        client.configureBlocking(false);

                        client.register(selector, SelectionKey.OP_READ);
                        System.out.println("ici");
                    } else if (myKey.isReadable()) {
                        byte[] message;
                        ByteBuffer buffer;
                        SocketChannel client = (SocketChannel) myKey.channel();
                        InputStream inputStream = client.socket().getInputStream();
                        byte[] bytes = new byte[1024];
                        int readCount = inputStream.read(bytes);
                        String receive = new String(bytes).trim();
                        if (readCount > 0) {
                            if (!receive.contains("ok")) {
                                message = messageToSend.getBytes();
                                buffer = ByteBuffer.wrap(message);
                                client.write(buffer);
                                messageToSend = "none";
                            }
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
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void addNodes() {
        if (!listNodestoAdd.isEmpty()) {
            messageToSend = ("add : [id = " + listNodestoAdd.get(0).getID()
                    + " , x = " + listNodestoAdd.get(0).getX()
                    + " , y = " + listNodestoAdd.get(0).getY()
                    + " , z = " + listNodestoAdd.get(0).getZ() + "]\n");
            listNodestoAdd.remove(0);
            if (!listNodestoAdd.isEmpty()) {
                messageToSend += ("add : [id = " + listNodestoAdd.get(0).getID()
                        + " , x = " + listNodestoAdd.get(0).getX()
                        + " , y = " + listNodestoAdd.get(0).getY()
                        + " , z = " + listNodestoAdd.get(0).getZ() + "]\n");
                listNodestoAdd.remove(0);
            }
        }
    }

    public void addComSens() {
        if (comRange != topology.getCommunicationRange() || sensRange != topology.getSensingRange()) {
            messageToSend = ("[cR : " + topology.getCommunicationRange() + ";" + "sR : " + topology.getSensingRange() + "]");
            comRange = topology.getCommunicationRange();
            sensRange = topology.getSensingRange();
        }
    }

    @Override
    public void onNodeAdded(Node node) {
        if (nbClient > 0) {
            messageToSend = ("add : [id = " + node.getID() + " , x = " + node.getX() + " , y = " + node.getY() + " , z = " + node.getZ() + "]");
        }
    }

    @Override
    public void onNodeRemoved(Node node) {
        if (nbClient > 0) {
            messageToSend = ("del : [id = " + node.getID() + " " + " , x = " + node.getX() + " , y = " + node.getY() + " , z = " + node.getZ() + "]");
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
