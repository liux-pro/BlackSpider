import com.jogamp.common.os.Platform;

public class OS {
    public static void main(String[] args) {
        System.out.println("Platform.getArchName() = " + Platform.getArchName());
        System.out.println("Platform.getOSAndArch() = " + Platform.getOSAndArch());
        System.out.println(Platform.getCPUFamily());

        System.out.println("System.mapLibraryName(\"libnativewindow_awt.so\") = " + System.mapLibraryName("libnativewindow_awt.so"));
    }
}
