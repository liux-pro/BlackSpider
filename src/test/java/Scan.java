import jogamp.nativetag.common.windows.amd64.TAG;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Scan {

    @Test
    public void test1123() throws IOException {

        TAG.class.getClassLoader();
        List<String> resourceFiles = getResourceFiles("natives");
        System.out.println(resourceFiles);
    }

    @Test
    public void test123() throws IOException {
        TAG tag = new TAG();
        List<String> resourceFiles = getResourceFiles("natives");
        System.out.println(resourceFiles);
    }

    @Test
    public void test222() throws IOException {
        TAG tag = new TAG();
        InputStream aNative = tag.getClass().getResourceAsStream("/natives");
        InputStreamReader inputStreamReader = new InputStreamReader(aNative);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        System.out.println(bufferedReader.readLine());
        System.out.println(bufferedReader.readLine());

    }


    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try {
            InputStream in = getResourceAsStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filenames;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
