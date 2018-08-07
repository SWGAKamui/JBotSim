package jbotsimx.network;

import jbotsim.Topology;
import jbotsimx.ui.JViewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;

public class Client {

private Topology topology;
    private SocketChannel client;
    private Boolean hasStarted = false;
    private IP ip = new IP();
    private StringGestion stringGestion;

    public Client(Topology topology) {
        this.topology = topology;
        stringGestion = new StringGestion(topology);
    }

    public void run(String serverIp, int port) {
        try {
            System.out.println("Clients try to connect ****");

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

            System.out.println("client is connected : " + client.isConnected() + "\n");


            if (client.isConnected())
                hasStarted = client.isConnected();

            while (client.isConnected()) {
                InputStream inputStream = client.socket().getInputStream();
                byte[] bytes = new byte[1024];
                int readCount = inputStream.read(bytes);
                String message = new String(bytes).trim();
                if (readCount > 0) {
                    if (readCount > 0) {
                        if (!message.contains("none") && (message.contains("del") || message.contains("add")|| message.contains("move") )
                                && message.contains("[id = ")&& message.contains(", x = ")&& message.contains(", y = ")&& message.contains(", z = ") && message.contains("]")) {
                            stringGestion.traitementMessage(message);
                            client.write(ByteBuffer.wrap("ok".getBytes()));
                        }
                        else if(!message.contains("none") && message.contains("[cR : ") && message.contains(";")&& message.contains("sR : ")&& message.contains("]")){
                            stringGestion.traitementMessage(message);
                            client.write(ByteBuffer.wrap("ok".getBytes()));
                        }
                        else{
                            client.write(ByteBuffer.wrap("not ok".getBytes()));
                        }
                    }
                }
            }
            client.close();
            System.out.println("client is closed");
        } catch (AsynchronousCloseException e){

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Topology getTopology() {
        return topology;
    }

    public boolean isCreated() {
        return hasStarted;
    }

    public void close() {
        try {
            client.socket().close();
            client.close();
            System.out.println("client is closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
