import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 */
public class PathTest {
    public static void main(String[] args) {
        System.getenv().entrySet().forEach(System.out::println);
        System.out.println(System.getProperty("user.dir"));
        System.out.println(System.getProperty("java.library.path"));
    }

    @Test
    public void test01() throws URISyntaxException {
        try {
            String libraryName = System.mapLibraryName("org/libturbojpeg/turbojpeg");
            System.out.println(libraryName);
            URL resource = PathTest.class.getClassLoader().getResource(libraryName);
            System.out.println(PathTest.class.getClassLoader().getResource(""));
            System.out.println(resource);
            System.load(new File(resource.toURI()).toString());
        }catch (URISyntaxException e){
            System.out.println(e);
        }

    }
}
