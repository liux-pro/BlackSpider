import com.jogamp.common.os.Platform;
import jogamp.nativetag.common.windows.amd64.TAG;
import org.junit.Test;
import pro.liux.blackspider.util.NativeLoader;

import java.io.File;
import java.net.URL;


public class OS {
    public static void main(String[] args) {
        System.out.println("Platform.getArchName() = " + Platform.getArchName());
        System.out.println("Platform.getOSAndArch() = " + Platform.getOSAndArch());
        System.out.println(Platform.getCPUFamily());

        System.out.println("System.mapLibraryName(\"libnativewindow_awt.so\") = " + System.mapLibraryName("libnativewindow_awt.so"));
    }

    @Test
    public void testGetResourcesDir() {
        System.out.println("natives" + File.separator + getOSAndArch());
        URL resource = TAG.class.getClassLoader().getResource("natives" + File.separator + getOSAndArch());
        System.out.println(resource);

    }

    private static File getResource(String file) {
        return new File(TAG.class.getClassLoader().getResource(file).getPath());
    }

    private static String getOSAndArch() {
        return Platform.getOSAndArch();
    }

    @Test
    public void testLibs() {
//        Reflections reflections = new Reflections("some.package", new ResourcesScanner());
//        Set<String> fileNames = reflections.getResources(Pattern.compile(".*\\.csv"));
    }

    @Test
    public void testJOGL() {
        NativeLoader.loadJogl();
    }



}
