package pro._91code.blackspider.dao;

import pro._91code.blackspider.bean.SpiderDatagramFrame;
import pro._91code.blackspider.util.UserDatagramProtocolUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.function.Supplier;

/**
 * @author LEGEND
 */
public class FrameDao {
    private final Supplier<DatagramPacket> supplier = new UserDatagramProtocolUtil(new InetSocketAddress(1689));
    private final SpiderDatagramFrame frame = new SpiderDatagramFrame();

    public FrameDao() throws IOException {

    }


    public SpiderDatagramFrame getFrame() {
        DatagramPacket datagramPacket;
        SpiderDatagramFrame temp;
        while (true) {
            datagramPacket = supplier.get();
            if ((temp = frame.feed(datagramPacket)) != null) {
                return temp;
            }
        }

    }
}
