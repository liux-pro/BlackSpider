package util;


import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.function.Supplier;

/**
 * @author LEGEND
 */
public class UserDatagramProtocolUtil implements Closeable, Supplier<DatagramPacket> {
    private final DatagramSocket datagramSocket;
    private static final int BUFFER_POOL_COUNT=1024*10;
    private final byte[][] bufferPool=new byte[BUFFER_POOL_COUNT][1024*2];
    private int bufferPoolPointer;
    public byte[] getBufferPool() {
        if (bufferPoolPointer>=BUFFER_POOL_COUNT) {
            bufferPoolPointer=0;
        }
        return bufferPool[bufferPoolPointer++];
    }

    public UserDatagramProtocolUtil(InetSocketAddress address) throws IOException {
        datagramSocket = new DatagramSocket(null);
        datagramSocket.setReuseAddress(true);
        datagramSocket.bind(address);
        datagramSocket.setReceiveBufferSize(1024*1024*10);
    }

    @Override
    public void close() throws IOException {
        datagramSocket.close();
    }

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public DatagramPacket get() {
        byte[] data = getBufferPool();
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
        try {
            datagramSocket.receive(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datagramPacket;
    }

    public static void main(String[] args) throws IOException {
        UserDatagramProtocolUtil userDatagramProtocolUtil = new UserDatagramProtocolUtil(new InetSocketAddress("127.0.0.1",1689));
//        DatagramPacket datagramPacket = userDatagramProtocolUtil.get();
//        System.out.println(Arrays.toString(datagramPacket.getData()));
        int i=0;
        while (userDatagramProtocolUtil.get().getLength()>0){
            System.out.println(++i);
        }
    }
}
