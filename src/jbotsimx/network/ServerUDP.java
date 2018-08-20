package jbotsimx.network;

import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.MovementListener;

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

public class ServerUDP implements MovementListener {
    ServerSocketChannel serverSocketChannel;
    private Topology topology;
    private String messageToSend = "none";
    private int nbClient = 0;
    private int nbClientSave = 0;
    private ArrayList<Integer> listIdToSend = new ArrayList<>();
    private List<Node> listNodestoAdd;
    private double comRange = 100;
    private double sensRange = 0;
    private Boolean exception = false;
    private Boolean hasCreatedServer = false;

    public ServerUDP(Topology topology) {
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
            System.out.println("****");

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



                        if (!messageToSend.contains("none")) {
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
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMove(Node node) {
        if (nbClient > 0) {
            if (!listIdToSend.contains(node.getID())) {
                if (!messageToSend.equals("none")) {
                    messageToSend += ("move : [id = " + node.getID() + ", x = " + node.getX() + ", y = " + node.getY() + ", z = " + node.getZ() + "]\n");
                } else {
                    messageToSend = ("move : [id = " + node.getID() + ", x = " + node.getX() + ", y = " + node.getY() + ", z = " + node.getZ() + "]\n");
                }
            }
            listIdToSend.add(node.getID());
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
