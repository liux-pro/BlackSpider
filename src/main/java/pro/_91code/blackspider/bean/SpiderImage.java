package pro._91code.blackspider.bean;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJDecompressor;
import org.libjpegturbo.turbojpeg.TJException;

import java.io.FileOutputStream;
import java.util.Arrays;


/**
 * @author LEGEND
 */
public class SpiderImage {
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

    public int getLineBytesSizeNoPadding() {
        return lineBytesSizeNoPadding;
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
            alignment = 4;
            if (imageSize < imageWidth * imageHeight * RGB24_BYTES_PER_PIXEL) {
                bitPerPixel = RGB24_BYTES_PER_PIXEL * 8;
            } else {
                bitPerPixel = RGB555_BYTES_PER_PIXEL * 8;
            }
            lineBytesSize = ((bitPerPixel * imageWidth + 31) / 32) * 4;

        } else if ("jpeg".equals(this.imageCompressionAlgorithm)) {
            alignment = 1;
            bitPerPixel = RGB24_BYTES_PER_PIXEL * 8;
            lineBytesSize = RGB24_BYTES_PER_PIXEL * imageWidth;


            try {
                TJDecompressor tjd = new TJDecompressor(image);
                byte[] rawData = new byte[tjd.getWidth() * tjd.getHeight() * 3];
                tjd.decompress(rawData, 0, 0, tjd.getWidth(), tjd.getWidth() * 3, tjd.getHeight(), TJ.PF_BGR, TJ.FLAG_FASTUPSAMPLE);
                this.image = rawData;
            } catch (TJException e) {
                e.printStackTrace();
            }

        }
        lineBytesSizeNoPadding=bitPerPixel/8*imageWidth;
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
