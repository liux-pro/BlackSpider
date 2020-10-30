package bean;

import java.net.DatagramPacket;

import static util.DataUtil.getInt;

public class SpiderDatagramFrameHead {
    private final byte[] data;
    private final SpiderDatagramFrameType type;
    private final int imageId;
    private final int dataLength;
    private final int serial;

    SpiderDatagramFrameHead(DatagramPacket datagramPacket){
        data = datagramPacket.getData();
        type = SpiderDatagramFrameType.getInstance(data[1]);
        imageId =  getInt(data,2,3);
        dataLength = getInt(data,4,5);
        serial = getInt(data,8,9);

    }




    public byte[] getData() {
        return data;
    }

    public SpiderDatagramFrameType getType() {
        return type;
    }

    public int getImageId() {
        return imageId;
    }

    public int getDataLength() {
        return dataLength;
    }

    public int getSerial() {
        return serial;
    }
}
