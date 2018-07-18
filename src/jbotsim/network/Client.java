package jbotsim.network;

import jbotsim.ui.JViewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;


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

            System.out.println("Waiting connection ***");
            while (!client.finishConnect()) {
                //wait connection
            }
            System.out.println("client connecté : " + client.isConnected());

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                InputStream inputStream = client.socket().getInputStream();

                byte[] bytes = new byte[1024];
                int readCount = inputStream.read(bytes);
                //int readCount = client.read(buffer);

                String result = new String(bytes).trim();
                if (readCount > 0) {
                    System.out.println(result);
                    jViewer.getJTopology().getTopology().fromString(result+"\n");

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
