import org.anarres.lzo.LzoAlgorithm;
import org.anarres.lzo.LzoDecompressor;
import org.anarres.lzo.LzoLibrary;
import org.anarres.lzo.lzo_uintp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class zloCompressionTest {
    public static void main(String[] args) throws IOException {

        InputStream in =new FileInputStream("E:\\Python\\demo1\\black_spider\\1.data");
        LzoAlgorithm algorithm = LzoAlgorithm.LZO1X;
        LzoDecompressor decompressor = LzoLibrary.getInstance().newDecompressor(algorithm, null);

        byte[] arr = new byte[100000];
        byte[] de = new byte[40000];

        int read1 = in.read(arr);
        System.out.println(read1);

        lzo_uintp lzo_uintp = new lzo_uintp();



        decompressor.decompress(arr,0,read1,de,0, lzo_uintp);


        System.out.println(lzo_uintp);
        System.out.println(Arrays.toString(de));

        int[] ints = new int[de.length];

        for (int i =0,h = 0; h <72; h++) {
            for (int w = 0; w < 144; w++) {
                ints[(72-h)*144+w]= (de[i*3] & 0xFF) | (de[i*3+1]& 0xFF << 8) | (de[i*3+2]& 0xFF << 16);
                i++;
            }
        }


        BufferedImage bufferedImage = new BufferedImage(144, 72, BufferedImage.TYPE_3BYTE_BGR);
        bufferedImage.setRGB(0, 0, 144, 72, ints, 0, 144);


        File outputfile = new File("image.bmp");
        ImageIO.write(bufferedImage, "bmp", outputfile);
    }
}
