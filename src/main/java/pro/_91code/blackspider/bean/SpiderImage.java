package pro._91code.blackspider.bean;

import com.jogamp.opengl.GL;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJDecompressor;
import org.libjpegturbo.turbojpeg.TJException;
import pro._91code.blackspider.util.MiniZloDecompressor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static pro._91code.blackspider.config.Debug.DEBUG;


/**
 * @author LEGEND
 */
public class SpiderImage {
    private static MiniZloDecompressor miniZloDecompressor = new MiniZloDecompressor(1366, 768);
    static TJDecompressor tjd;
    static {
        try {
            tjd = new TJDecompressor();
        } catch (TJException e) {
            e.printStackTrace();
        }
    }
    private static final int RGB24_BYTES_PER_PIXEL = 3;
    private static final int RGB555_BYTES_PER_PIXEL = 2;
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
    private String imageCompressionAlgorithm;
    private int imageWidth;
    private int imageHeight;
    private int alignment;
    private int lineBytesSize;
    private int bitPerPixel;
    private int lineBytesSizeNoPadding;
    private int RGBFormat;

    public int getLineBytesSizeNoPadding() {
        return lineBytesSizeNoPadding;
    }

    public int getRGBFormat() {
        return RGBFormat;
    }

    public SpiderImage(int imageId, int screenWidth, int screenHeight, int mouseX, int mouseY, int paintX1, int paintY1, int paintX2, int paintY2, int imageSize, byte[] image, String imageCompressionAlgorithm) {
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
        imageWidth = this.getPaintX2() - this.getPaintX1();
        imageHeight = this.getPaintY2() - this.getPaintY1();
        if ("mlzo".equals(this.imageCompressionAlgorithm)) {
            RGBFormat = GL.GL_BGR;
            alignment = 4;
            byte[] decompress = miniZloDecompressor.decompress(image, image.length);

            if (decompress.length >= imageWidth * imageHeight * RGB24_BYTES_PER_PIXEL) {
                bitPerPixel = RGB24_BYTES_PER_PIXEL * 8;
                this.image=decompress;
            } else {
                bitPerPixel = RGB555_BYTES_PER_PIXEL * 8;
                alignment = 1;
                byte[] bytes = new byte[imageWidth * imageHeight * RGB24_BYTES_PER_PIXEL];

                if (imageWidth * imageHeight * 2 == decompress.length) {
                    for (int i = 0, h = 0; h < imageHeight; h++) {
                        for (int w = 0; w < imageWidth; w++) {

                            int r = (decompress[i * 2 + 1] & 0xFF & 0x7C) << 1;
                            int g = ((decompress[i * 2 + 1] & 0xFF & 0x03) << 6) + ((decompress[i * 2] & 0xFF & 0xE0) >> 2);
                            int b = (decompress[i * 2] & 0xFF & 0x1F) << 3;
                            r=r+((byte)(r&0xFF)>>>5);
                            g=g+((byte)(g&0xFF)>>>5);
                            b=b+((byte)(b&0xFF)>>>5);
//                            ints[(imageHeight - h - 1) * imageWidth + w] = b | (g << 8) | (r << 16);
                            int index = (imageHeight - h - 1) * imageWidth + (imageWidth-w);
                            index=bytes.length/3-index;
                            bytes[3 * index] = (byte) b;
                            bytes[3 * index + 1] = (byte) g;
                            bytes[3 * index + 2] = (byte) r;
                            i++;
                        }
                    }

                } else if (imageWidth * imageHeight * 2 + imageHeight * 2 == decompress.length) {

                    for (int i = 0, h = 0; h < imageHeight; h++) {
                        for (int w = 0; w < imageWidth; w++) {

                            int r = (decompress[i * 2 + 1 + 2 * h] & 0xFF & 0x7C) << 1;
                            int g = ((decompress[i * 2 + 1 + 2 * h] & 0xFF & 0x03) << 6) + ((decompress[i * 2 + 2 * h] & 0xFF & 0xE0) >> 2);
                            int b = (decompress[i * 2 + 2 * h] & 0xFF & 0x1F) << 3;
                            r=r+((byte)(r&0xFF)>>>5);
                            g=g+((byte)(g&0xFF)>>>5);
                            b=b+((byte)(b&0xFF)>>>5);

//                            ints[(imageHeight - h - 1) * imageWidth + w] = b | (g << 8) | (r << 16);
                            int index = (imageHeight - h - 1) * imageWidth +  (imageWidth-w);
                            index=bytes.length/3-index;
                            bytes[3 * index] = (byte) b;
                            bytes[3 * index + 1] = (byte) g;
                            bytes[3 * index + 2] = (byte) r;
                            i++;
                        }
                    }

                }
                this.image=bytes;
            }
            this.imageSize=this.image.length;
            lineBytesSize = ((bitPerPixel * imageWidth + 31) / 32) * 4;
        } else if ("jpeg".equals(this.imageCompressionAlgorithm)) {
            RGBFormat = GL.GL_RGB;
            alignment = 1;
            bitPerPixel = RGB24_BYTES_PER_PIXEL * 8;
            lineBytesSize = RGB24_BYTES_PER_PIXEL * imageWidth;


            try {
                tjd.setSourceImage(image,imageSize);
                byte[] rawData = new byte[tjd.getWidth() * tjd.getHeight() * 3];
                tjd.decompress(rawData, 0, 0, tjd.getWidth(), tjd.getWidth() * 3, tjd.getHeight(), TJ.PF_BGR, TJ.FLAG_FASTUPSAMPLE);
                this.image = rawData;
            } catch (TJException e) {
                e.printStackTrace();
            }
            lineBytesSizeNoPadding = bitPerPixel / 8 * imageWidth;

            byte[] exchangeBuffer = new byte[this.image.length];

            for (int h = 0; h < this.getImageHeight(); h++) {
                System.arraycopy(this.getImage(), h * this.getLineBytesSize(),
                        exchangeBuffer, (this.getImageHeight() - h - 1) * this.getLineBytesSize(),
                        this.getLineBytesSizeNoPadding()
                );
            }
            this.image = exchangeBuffer;

        }else if ("zlib".equals(this.imageCompressionAlgorithm)){
            if (DEBUG) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(this.imageId + "zlib");
                    fileOutputStream.write(image);
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public int getLineBytesSize() {
        return lineBytesSize;
    }

    public void setLineBytesSize(int lineBytesSize) {
        this.lineBytesSize = lineBytesSize;
    }

    public int getBitPerPixel() {
        return bitPerPixel;
    }

    public void setBitPerPixel(int bitPerPixel) {
        this.bitPerPixel = bitPerPixel;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getMouseX() {
        return mouseX;
    }

    public void setMouseX(int mouseX) {
        this.mouseX = mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public void setMouseY(int mouseY) {
        this.mouseY = mouseY;
    }

    public int getPaintX1() {
        return paintX1;
    }

    public void setPaintX1(int paintX1) {
        this.paintX1 = paintX1;
    }

    public int getPaintY1() {
        return paintY1;
    }

    public void setPaintY1(int paintY1) {
        this.paintY1 = paintY1;
    }

    public int getPaintX2() {
        return paintX2;
    }

    public void setPaintX2(int paintX2) {
        this.paintX2 = paintX2;
    }

    public int getPaintY2() {
        return paintY2;
    }

    public void setPaintY2(int paintY2) {
        this.paintY2 = paintY2;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageCompressionAlgorithm() {
        return imageCompressionAlgorithm;
    }

    public void setImageCompressionAlgorithm(String imageCompressionAlgorithm) {
        this.imageCompressionAlgorithm = imageCompressionAlgorithm;
    }
}
