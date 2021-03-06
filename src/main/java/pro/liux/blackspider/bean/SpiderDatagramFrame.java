package pro.liux.blackspider.bean;


import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJDecompressor;
import pro.liux.blackspider.util.MiniZloDecompressor;
import pro.liux.blackspider.config.Debug;

import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.util.Arrays;

import static pro.liux.blackspider.util.DataUtil.getInt;


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
    private ImageData imageData;
    private String imageCompressionAlgorithm;

    public SpiderDatagramFrame() {
    }



    public int getPaintX2() {
        return paintX2;
    }

    public int getPaintY2() {
        return paintY2;
    }

    public SpiderImage feed(DatagramPacket datagramPacket) {

        SpiderDatagramFrameHead frameHead = new SpiderDatagramFrameHead(datagramPacket);
        byte[] data = datagramPacket.getData();

        switch (frameHead.getType()) {
            case FIRST_FRAME:
                if (Debug.DEBUG) {
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
                if (Debug.DEBUG) {
                    System.out.println("身份识别包");
                }
                break;
            case UNKNOWN:
            default:
                if (Debug.DEBUG) {
                    System.out.println("unknown package");
                }
                break;
        }
        SpiderImage temp = null;
        if (currentImageSize == imageSize && imageSize != 0) {
            temp = new SpiderImage(imageId, screenWidth, screenHeight, mouseX, mouseY, paintX1, paintY1, paintX2, paintY2, imageSize, image, imageCompressionAlgorithm);
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
        imageData = null;

    }

    private boolean checkImageId(int currentImageId) {
        return this.imageId == currentImageId;
    }

    public ImageData getImageData() {
        if (imageData != null) {
            return imageData;
        }
        byte[] image = getImage();
        ImageData imageData = null;
        long l = System.nanoTime();
        try {

            if ("jpeg".equals(this.imageCompressionAlgorithm)) {
                TJDecompressor tjd = new TJDecompressor(image);

                PaletteData swtPalette = new PaletteData(0xff, 0xff00, 0xff0000);
                byte[] rawData = new byte[tjd.getWidth() * tjd.getHeight() * 3];
                tjd.decompress(rawData, 0, 0, tjd.getWidth(), tjd.getWidth() * 3, tjd.getHeight(), TJ.PF_BGR, TJ.FLAG_FASTUPSAMPLE);
                imageData = new ImageData(tjd.getWidth()
                        , tjd.getHeight(), 24, swtPalette, tjd.getWidth(), rawData);

                //  mzlo is  http://www.oberhumer.com/opensource/lzo/
            } else if ("mlzo".equals(this.imageCompressionAlgorithm)) {

                byte[] decompress = miniZloDecompressor.decompress(image, image.length);


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

                } else if (imageWidth * imageHeight * 3 + imageHeight * 2 == decompress.length) {
                    //3 byte line + 00 00
                    for (int i = 0, h = 0; h < imageHeight; h++) {
                        for (int w = 0; w < imageWidth; w++) {
                            ints[(imageHeight - h - 1) * imageWidth + w] = (decompress[i * 3 + h * 2] & 0xFF) | ((decompress[i * 3 + 1 + h * 2] & 0xFF) << 8) | ((decompress[i * 3 + 2 + h * 2] & 0xFF) << 16);
                            i++;
                        }
                    }
                } else {
                    if (Debug.DEBUG) {
                        System.out.println(Arrays.toString(decompress));
                        System.out.println("de.len" + decompress.length);
                        System.out.println("w" + imageWidth);
                        System.out.println("h" + imageHeight);
                        System.out.println(paintX2 - paintX1);
                        System.out.println(paintY2 - paintY1);
                    }
                }
//


                PaletteData swtPalette = new PaletteData(0xff, 0xff00, 0xff0000);
                byte[] rawData = new byte[imageWidth * imageHeight * 3];
                imageData = new ImageData(imageWidth
                        , imageHeight, 24, swtPalette, imageWidth, rawData);

                for (int h = 0; h < imageHeight; h++) {
                    imageData.setPixels(0, h, imageWidth, ints, h*imageWidth);
                }


                if (Debug.DEBUG) {
                    System.out.println("解析mlzo用时" + (System.nanoTime() - l) / 100000 + "毫秒");
                }
            } else {
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
        this.imageData = imageData;
        this.image = null;
//        return imageData;
        return imageData;
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