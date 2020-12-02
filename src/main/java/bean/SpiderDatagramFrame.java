package bean;


import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJDecompressor;
import util.MiniZloDecompressor;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.util.Arrays;

import static config.Debug.DEBUG;
import static util.DataUtil.getInt;


/**
 * @author LEGEND
 */
public class SpiderDatagramFrame {
    private static MiniZloDecompressor miniZloDecompressor = new MiniZloDecompressor(1366, 768);
    private int imageId;
    private int screenWidth;
    private int screenHeight;
    private int mouseX;
    private int mouseY;
    private int paintX1;
    private int paintY1;
    private int paintX2;
    private int paintY2;
    private int imageSize;
    private byte[] image;
    private int currentImageSize;
    private BufferedImage bufferedImage;
    private String imageCompressionAlgorithm;

    public SpiderDatagramFrame() {
    }

    public SpiderDatagramFrame(int imageId, int screenWidth, int screenHeight, int mouseX, int mouseY, int paintX1, int paintY1, int paintX2, int paintY2, int imageSize, byte[] image, String imageCompressionAlgorithm) {
        this.imageId = imageId;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.paintX1 = paintX1;
        this.paintY1 = paintY1;
        this.paintX2 = paintX2;
        this.paintY2 = paintY2;
        this.imageSize = imageSize;
        this.image = image;
        this.imageCompressionAlgorithm = imageCompressionAlgorithm;
    }

    public int getPaintX2() {
        return paintX2;
    }

    public int getPaintY2() {
        return paintY2;
    }

    public SpiderDatagramFrame feed(DatagramPacket datagramPacket) {

        SpiderDatagramFrameHead frameHead = new SpiderDatagramFrameHead(datagramPacket);
        byte[] data = datagramPacket.getData();

        switch (frameHead.getType()) {
            case FIRST_FRAME:
                if (DEBUG) {
                    if (imageId != 0) {
                        System.out.println(frameHead.getImageId());
                        System.out.println(this);
                    }
                }
                reInit();

                imageId = frameHead.getImageId();
                screenWidth = getInt(data, 16, 17);
                screenHeight = getInt(data, 20, 21);
                paintX1 = getInt(data, 28, 29);
                paintY1 = getInt(data, 32, 33);
                paintX2 = getInt(data, 36, 37);
                paintY2 = getInt(data, 40, 41);
                mouseX = getInt(data, 48, 49);
                mouseY = getInt(data, 52, 53);
                imageSize = getInt(data, 108, 109, 110);
                image = new byte[imageSize];
                System.arraycopy(data, 128, image, 0, frameHead.getDataLength() - 128);
                currentImageSize += (frameHead.getDataLength() - 128);
                imageCompressionAlgorithm = new String(data, 104, 4);


                break;
            case MIDDLE_FRAME:
                if (!checkImageId(frameHead.getImageId())) {
                    return null;
                }
                System.arraycopy(data, 12, image, (frameHead.getSerial() - 2) * 1428 + 1312, frameHead.getDataLength() - 12);
                currentImageSize += (frameHead.getDataLength() - 12);
                return null;
            case LAST_FRAME:
                if (!checkImageId(frameHead.getImageId())) {
                    return null;
                }
                System.arraycopy(data, 12, image, image.length - (frameHead.getDataLength() - 12), frameHead.getDataLength() - 12);
                currentImageSize += (frameHead.getDataLength() - 12);
                break;
            case IDENTIFICATION:
                if (DEBUG) {
                    System.out.println("身份识别包");
                }
                break;
            case UNKNOWN:
            default:
                if (DEBUG) {
                    System.out.println("unknown package");
                }
                break;
        }
        SpiderDatagramFrame temp = null;
        if (currentImageSize == imageSize && imageSize != 0) {
            temp = new SpiderDatagramFrame(imageId, screenWidth, screenHeight, mouseX, mouseY, paintX1, paintY1, paintX2, paintY2, imageSize, image, imageCompressionAlgorithm);
            reInit();
        }
        return temp;

    }

    private void reInit() {
        imageId = 0;

        screenWidth = 0;
        screenHeight = 0;
        mouseX = 0;
        mouseY = 0;
        paintX1 = 0;
        paintY1 = 0;
        paintX2 = 0;
        paintY2 = 0;
        imageSize = 0;
        image = null;
        currentImageSize = 0;
        imageCompressionAlgorithm = null;

    }

    private boolean checkImageId(int currentImageId) {
        return this.imageId == currentImageId;
    }

    public BufferedImage getBufferedImage() {
        if (bufferedImage != null) {
            return bufferedImage;
        }
        byte[] image = getImage();
        BufferedImage read = null;
        long l = System.nanoTime();
        try {

            if ("jpeg".equals(this.imageCompressionAlgorithm)) {
                TJDecompressor tjd = new TJDecompressor(image);
                read = new BufferedImage(tjd.getWidth(), tjd.getHeight(),
                        BufferedImage.TYPE_3BYTE_BGR);
                tjd.decompress(read, TJ.FLAG_FASTUPSAMPLE);

               if (DEBUG){
                   System.out.println("纯解析jpeg用时" + (System.nanoTime() - l) / 100000 + "毫秒");
               }
                ColorModel colorModel = read.getColorModel();
                WritableRaster swapped = read.getRaster().
                        createWritableChild(0, 0, read.getWidth(), read.getHeight(), 0, 0,
                                // switch rgb channel ，default order is 0, 1, 2
                                new int[]{2, 1, 0});
                read = new BufferedImage(colorModel, swapped, colorModel.isAlphaPremultiplied(), null);


                //  mzlo is  http://www.oberhumer.com/opensource/lzo/
            } else if ("mlzo".equals(this.imageCompressionAlgorithm)) {

                byte[] decompress = miniZloDecompressor.decompress(image, image.length);


//                int imageWidth = DataUtil.roundUp(paintX2 - paintX1);;
//                int imageHeight = DataUtil.roundUp(paintY2 - paintY1);

                int imageWidth = paintX2 - paintX1;

                int imageHeight = paintY2 - paintY1;

                int[] ints = new int[imageHeight * imageWidth];


                if (imageWidth * imageHeight * 3 == decompress.length) {
                    for (int i = 0, h = 0; h < imageHeight; h++) {
                        for (int w = 0; w < imageWidth; w++) {
                            ints[(imageHeight - h - 1) * imageWidth + w] = (decompress[i * 3] & 0xFF) | ((decompress[i * 3 + 1] & 0xFF) << 8) | ((decompress[i * 3 + 2] & 0xFF) << 16);
                            i++;
                        }
                    }
                    /*rgb 16 bit channel is 2*/
                } else if (imageWidth * imageHeight * 2 == decompress.length) {
                    for (int i = 0, h = 0; h < imageHeight; h++) {
                        for (int w = 0; w < imageWidth; w++) {

                            int r = (decompress[i * 2 + 1] & 0xFF & 0x7C) << 1;
                            int g = ((decompress[i * 2 + 1] & 0xFF & 0x03) << 6) + ((decompress[i * 2] & 0xFF & 0xE0) >> 2);
                            int b = (decompress[i * 2] & 0xFF & 0x1F) << 3;

                            ints[(imageHeight - h - 1) * imageWidth + w] = b | (g << 8) | (r << 16);
                            i++;
                        }
                    }

                } else if (imageWidth * imageHeight * 3 + imageHeight == decompress.length) {

                    for (int i = 0, h = 0; h < imageHeight; h++) {
                        for (int w = 0; w < imageWidth; w++) {
                            ints[(imageHeight - h - 1) * imageWidth + w] = (decompress[i * 3 + h] & 0xFF) | ((decompress[i * 3 + 1 + h] & 0xFF) << 8) | ((decompress[i * 3 + 2 + h] & 0xFF) << 16);
                            i++;
                        }
                    }
                } else if (imageWidth * imageHeight * 2 + imageHeight * 2 == decompress.length) {

                    for (int i = 0, h = 0; h < imageHeight; h++) {
                        for (int w = 0; w < imageWidth; w++) {

                            int r = (decompress[i * 2 + 1 + 2 * h] & 0xFF & 0x7C) << 1;
                            int g = ((decompress[i * 2 + 1 + 2 * h] & 0xFF & 0x03) << 6) + ((decompress[i * 2 + 2 * h] & 0xFF & 0xE0) >> 2);
                            int b = (decompress[i * 2 + 2 * h] & 0xFF & 0x1F) << 3;

                            ints[(imageHeight - h - 1) * imageWidth + w] = b | (g << 8) | (r << 16);
                            i++;
                        }
                    }

                }
                else if (imageWidth * imageHeight * 3 + imageHeight * 2 == decompress.length){
                    //3 byte line + 00 00
                    for (int i = 0, h = 0; h < imageHeight; h++) {
                        for (int w = 0; w < imageWidth; w++) {
                            ints[(imageHeight - h - 1) * imageWidth + w] = (decompress[i * 3 + h*2] & 0xFF) | ((decompress[i * 3 + 1 + h*2] & 0xFF) << 8) | ((decompress[i * 3 + 2 + h*2] & 0xFF) << 16);
                            i++;
                        }
                    }
                }

                else {
                    System.out.println(Arrays.toString(decompress));
                    System.out.println("de.len" + decompress.length);
                    System.out.println("w" + imageWidth);
                    System.out.println("h" + imageHeight);
                    System.out.println(paintX2 - paintX1);
                    System.out.println(paintY2 - paintY1);
                }
//
                read = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);
                read.setRGB(0, 0, imageWidth, imageHeight, ints, 0, imageWidth);
              // System.arraycopy(ints,0, read.getData(),0,ints.length);

            if (DEBUG){
                System.out.println("解析mlzo用时" + (System.nanoTime() - l) / 100000 + "毫秒");
            }
            }else {
                System.err.println(Arrays.toString(image));
                System.err.println(paintX2 - paintX1);
                System.err.println(paintY2 - paintY1);
                System.err.println(imageCompressionAlgorithm);
                FileOutputStream fileOutputStream = new FileOutputStream("123.data");
                fileOutputStream.write(image);
                fileOutputStream.close();
                Runtime.getRuntime().exit(0);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        this.bufferedImage = read;
        this.image = null;
        return read;

    }

    public int getImageId() {
        return imageId;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getPaintX1() {
        return paintX1;
    }

    public int getPaintY1() {
        return paintY1;
    }

    public int getImageSize() {
        return imageSize;
    }

    public byte[] getImage() {
        return image;
    }

    public int getCurrentImageSize() {
        return currentImageSize;
    }

    @Override
    public String toString() {
        return "SpiderDatagramFrame{" +
                "imageId=" + imageId +
                ", screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                ", mouseX=" + mouseX +
                ", mouseY=" + mouseY +
                ", paintX=" + paintX1 +
                ", paintY=" + paintY1 +
                ", imageSize=" + imageSize +
                ", image=" + Arrays.toString(image) +
                ", currentImageSize=" + currentImageSize +
                '}';
    }
}