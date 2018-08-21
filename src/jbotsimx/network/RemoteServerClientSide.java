package jbotsimx.network;

import jbotsim.Node;
import jbotsim.Topology;
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

public class RemoteServerClientSide {
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

    public RemoteServerClientSide(Topology topology) {
        listNodestoAdd = topology.getNodes();
        this.topology = topology;
    }
    public int getNbClientSave(){
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
            System.out.println("Remote Server is created");

            while (!exception) {
                hasCreatedServer = true;
                selector.selectNow();

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

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
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
