package jbotsimx.network;

import jbotsim.Node;
import jbotsim.Topology;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RemoteServer {
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
    private StringGestion stringGestion;
    int nbNodes = 0;
    public RemoteServer(Topology topology) {
        stringGestion = new StringGestion(topology);
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
            System.out.println("Remote Server is created");

            while (!exception) {
                hasCreatedServer = true;
                selector.selectNow();

                Set<SelectionKey> Keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = Keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey myKey = iterator.next();
                    if (myKey.isReadable()) {
                        SocketChannel client = serverSocketChannel.accept();
                        nbClient++;
                        client.configureBlocking(false);

                        client.register(selector, SelectionKey.OP_READ);
                    } else if (myKey.isReadable()) {
                        SocketChannel client = (SocketChannel) myKey.channel();
                        InputStream inputStream = client.socket().getInputStream();
                        byte[] bytes = new byte[1024];
                        System.out.println("ici");
                        int readCount = inputStream.read(bytes);
                        String message = new String(bytes).trim();
                        if (readCount > 0) {
                            if (!message.contains("none")) {

                                stringGestion.traitementMessage(message);
                            }
                        }
                    }
                    Thread.sleep(30);
                    iterator.remove();
                }
                if(nbNodes != topology.getNodes().size()) {
                    nbNodes = topology.getNodes().size();
                    System.out.println("nbNodes ==  "+nbNodes);
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
