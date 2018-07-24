package jbotsim.network;

import jbotsim.Node;
import jbotsimx.ui.JViewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {
    private JViewer jViewer;
    private int id;
    private double x;
    private double y;
    private double z;
    private int ip1;
    private int ip2;
    private int ip3;
    private int ip4;

    public Client(JViewer jViewer) {
        this.jViewer = jViewer;
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
            //SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 1111));
            System.out.println("Clients try to connect ****");
            parseIntIP(serverIp);
            byte[] address = {(byte)ip1, (byte)ip2, (byte) ip3, (byte) ip4};
            InetAddress ip = InetAddress.getByAddress(address);
            //SocketChannel client = SocketChannel.open(new InetSocketAddress(ip, 51423));
            SocketChannel client = SocketChannel.open(new InetSocketAddress(ip, 7777));
            //client.configureBlocking(false);
            client.socket().setTcpNoDelay(true);

            System.out.println("Waiting for connection ***");
            while (!client.finishConnect()) {
                //wait connection
                System.out.print("*");
            }
            System.out.println("client is connected : " + client.isConnected() + "\n");

            while (true) {
                InputStream inputStream = client.socket().getInputStream();

                byte[] bytes = new byte[1024];

                int readCount = inputStream.read(bytes);

                String message = new String(bytes).trim();
                if (readCount > 0) {
                    if (!message.contains("none")) {
                        traitementMessage(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void traitementMessage(String message) {
        String[] lines = message.split("\r\n|\r|\n");
        if (message.contains("move")) {
            for (String line : lines) {
                Node n = jViewer.getJTopology().getTopology().findNodeById(id);
                if (n != null) {
                    moveNode(id, x, y, z);
                } else {
                    addNode(id, x, y, z);
                }
                getProperties(line);
            }
        } else if (message.contains("add")) {
            for (String line : lines) {
                getProperties(line);
                addNode(id, x, y, z);
            }
        } else if (message.contains("del")) {
            getProperties(message);
            delNode(id);
        } else if (message.contains("color")) {
            colorNode(message);
        } else if (message.contains("size")) {
            sizeNode(message);
        } else if (message.contains("cR") || message.contains("sR")) {
            jViewer.getJTopology().getTopology().setCommunicationRange(Double.parseDouble(message.substring(message.indexOf(":") + 1, message.indexOf(";")).trim()));
            jViewer.getJTopology().getTopology().setSensingRange(Double.parseDouble(message.substring(message.indexOf("sR :") + 4, message.indexOf("]")).trim()));
        }
    }

    private void colorNode(String message) {
        id = Integer.parseInt(message.substring(message.indexOf("id") + 5, message.indexOf(",")).trim());
        int color = Integer.parseInt(message.substring(message.indexOf(","), message.indexOf("]")).trim());

        jViewer.getJTopology().getTopology().findNodeById(id).setIntColor(color);
        System.out.println(color);
    }

    private void sizeNode(String message) {
        id = Integer.parseInt(message.substring(message.indexOf("id") + 5, message.indexOf(",")).trim());
        int size = Integer.parseInt(message.substring(message.indexOf(","), message.indexOf("]")).trim());
        jViewer.getJTopology().getTopology().findNodeById(id).setSize(size);
    }


    private void getProperties(String message) {
        try {
            if (message.contains("[") && message.contains("id") && message.contains("x") && message.contains("y") && message.contains("z") && message.contains("]")
                    && !message.contains("sR") && !message.contains("cR") && !message.contains("color") && !message.contains("size")) {
                id = Integer.parseInt(message.substring(message.indexOf("id") + 5, message.indexOf(", x")).trim());
                x = Double.parseDouble(message.substring(message.indexOf("x") + 3, message.indexOf(", y")).trim());
                y = Double.parseDouble(message.substring(message.indexOf("y") + 3, message.indexOf(", z")).trim());
                z = Double.parseDouble(message.substring(message.indexOf("z") + 3, message.indexOf("]")).trim());
            }
        }catch(StringIndexOutOfBoundsException ignored){

        }
    }

    private void addNode(int id, double x, double y, double z) {
        if (jViewer.getJTopology().getTopology().findNodeById(id) == null) {
            Node node = new Node();
            node.setID(id);
            node.setLocation(x, y, z);
            jViewer.getJTopology().getTopology().addNode(node);
        }
    }

    private void delNode(int id) {
        if (jViewer.getJTopology().getTopology().findNodeById(id) != null) {
            jViewer.getJTopology().getTopology().removeNode(jViewer.getJTopology().getTopology().findNodeById(id));
        }
    }

    private void moveNode(int id, double x, double y, double z) {
        jViewer.getJTopology().getTopology().findNodeById(id).setLocation(x, y, z);
    }
}
