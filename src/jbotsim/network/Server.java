package jbotsim.network;

import jbotsim.ui.JViewer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    JViewer jViewer;

    public Server(JViewer jViewer) {
        this.jViewer = jViewer;
        jViewer.getJTopology().getTopology().addNode(100, 100);
        jViewer.getJTopology().getTopology().addNode(100, 100);
        jViewer.getJTopology().getTopology().addNode(100, 100);
        jViewer.getJTopology().getTopology().addNode(100, 100);
    }

    public void run() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress("localhost", 1111));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Set<SelectionKey> Keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = Keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey myKey = iterator.next();
                    if (myKey.isAcceptable()) {
                        SocketChannel client = serverSocketChannel.accept();
                        client.configureBlocking(false);

                        client.register(selector, SelectionKey.OP_WRITE);
                    } else if (myKey.isWritable()) {
                        SocketChannel client = (SocketChannel) myKey.channel();

                        byte[] message = (jViewer.getJTopology().getTopology().toString()).getBytes();
                       // System.out.println(jViewer.getJTopology().getTopology().toString()  + " end");
                        ByteBuffer buffer = ByteBuffer.wrap(message);

                        client.write(buffer);

                        System.out.println("send ***");
                    }
                    Thread.sleep(30);
                    iterator.remove();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
