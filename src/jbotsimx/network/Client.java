package jbotsimx.network;

import jbotsimx.ui.JViewer;
import test.TestNetwork;

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

    private StringGestion stringGestion;

    public Client(JViewer jViewer) {
        this.jViewer = jViewer;
        stringGestion = new StringGestion(this.jViewer);
    }

    public void run(String serverIp) {
        try {
            System.out.println("Clients try to connect ****");
            StringGestion.parseIntIP(serverIp, ip1, ip2, ip3, ip4);
            byte[] address = {(byte) ip1, (byte) ip2, (byte) ip3, (byte) ip4};
            InetAddress ip = InetAddress.getByAddress(address);
            SocketChannel client = SocketChannel.open(new InetSocketAddress(ip, 7777));
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
                        stringGestion.traitementMessage(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
