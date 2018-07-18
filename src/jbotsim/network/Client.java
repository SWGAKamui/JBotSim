package jbotsim.network;

import jbotsim.Node;
import jbotsim.ui.JViewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;


public class Client {
    JViewer jViewer;

    public Client(JViewer jViewer) {
        this.jViewer = jViewer;
    }


    public void run() {
        try {
            SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 1111));
            //client.configureBlocking(false);
            client.socket().setTcpNoDelay(true);

            System.out.println("Waiting for connection ***");
            while (!client.finishConnect()) {
                //wait connection
                System.out.print("*");
            }
            System.out.println("\n");
            System.out.println("client is connected : " + client.isConnected());

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
        int id = Integer.parseInt(message.substring(message.indexOf("id = ") + 5, message.indexOf(" , x = ") - 1));
        double x = Double.parseDouble(message.substring(message.indexOf("x = ") + 4, message.indexOf(" , y = ")));
        double y = Double.parseDouble(message.substring(message.indexOf("y = ") + 4, message.indexOf(" , z = ")));
        double z = Double.parseDouble(message.substring(message.indexOf("z = ") + 4, message.indexOf("]")));

        if (message.contains("move")) {
            Node n = jViewer.getJTopology().getTopology().findNodeById(id);
            if (n != null)
                moveNode(id, x, y, z);
            else {
                addNode(id, x, y, z);
            }
        } else if (message.contains("add")) {
            addNode(id, x, y, z);

        } else if (message.contains("del")) {
            delNode(id);
        } else if (!message.equals("none")) {
            System.out.println(message);
        }
    }

    private void addNode(int id, double x, double y, double z) {
        System.out.println("add");

        Node node = new Node();
        node.setID(id);
        node.setLocation(x, y, z);
        jViewer.getJTopology().getTopology().addNode(node);
    }

    private void delNode(int id) {

        if (jViewer.getJTopology().getTopology().findNodeById(id) != null) {
            jViewer.getJTopology().getTopology().removeNode(jViewer.getJTopology().getTopology().findNodeById(id));
            System.out.println("del");
        }
    }

    private void moveNode(int id, double x, double y, double z) {
        System.out.println("move");
        jViewer.getJTopology().getTopology().findNodeById(id).setLocation(x, y, z);
    }
}
