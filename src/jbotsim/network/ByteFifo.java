package jbotsim.network;


import java.util.Arrays;

/**
 * source : https://www.programcreek.com/java-api-examples/index.php?source_dir=tor-research-framework-master/src/main/java/tor/util/ByteFifo.java
 */
public class ByteFifo {
    byte buffer[];
    int in = 0, out = 0;
    int count = 0;

 /*test code
     * ByteFifo fifo = new ByteFifo(10);
 for(int i=0; i<5; i++) {
  fifo.put("hello".getBytes());
  System.out.println(new String(fifo.get(4)));
 }*/

    public ByteFifo(int capacity) {
        buffer = new byte[capacity];
    }

    public boolean isEmpty() {
        return in == out;
    }

    public synchronized void put(byte[] toput) {
        for (byte b : toput) {
            buffer[in] = b;
            in = (in + 1) % buffer.length;
            count++;
            if (count >= buffer.length)
                throw new RuntimeException("buffer overflow");
        }
    }

    public int available() {
        return count;
    }

    // bytes = -1 for unlimited
    public synchronized byte[] get(int bytes) {
        byte buf[] = new byte[buffer.length];
        int cnt = 0;
        while (count > 0 && (bytes == -1 || cnt < bytes)) {
            buf[cnt++] = buffer[out];
            count--;
            out = (out + 1) % buffer.length;
        }
        return Arrays.copyOfRange(buf, 0, cnt);
    }

}
