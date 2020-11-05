import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestSend {
    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 5000; i++) {
            FileInputStream fileInputStream = new FileInputStream(new File("E:\\pic\\pic\\"+i+".jpeg"));

            fileInputStream.close();
        }
    }
}
