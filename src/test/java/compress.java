import java.io.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;

public class compress {
    public static void main(String[] args) throws Exception {
        inflate(new File ("/home/legend/BlackSpider/26947zlib"),new File ("/home/legend/BlackSpider/26947zlib.raw"));
    }
    /**
     * 解压deflate格式文件
     * @param src 源文件
     * @param target 目标文件
     */
    private static void inflate(File src, File target){
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        InflaterOutputStream inflaterOutputStream = null;
        try {
            fileInputStream = new FileInputStream(src);
            fileOutputStream = new FileOutputStream(target);
            inflaterOutputStream = new InflaterOutputStream(fileOutputStream);

            byte[] b = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(b)) != -1) {
                inflaterOutputStream.write(b, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                inflaterOutputStream.close();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}