package pro.liux.blackspider.util;

import com.jogamp.common.jvm.JNILibLoaderBase;
import cz.adamh.utils.NativeUtils;
import org.graalvm.nativeimage.ImageInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class NativeLoader {
    public static List<String> WINDOWS= Arrays.asList("nativewindow_awt", "jogl_desktop", "gluegen-rt", "jogl_mobile", "nativewindow_win32");

    /**
     * according to the source code of jogl add some breakpoints at {@link com.jogamp.common.jvm.JNILibLoaderBase},
     * JOGL load native libraries by unzip the jar file which contains .dll .so, whatever.
     * That make sense when in stander jre.
     *
     * But in native-image,there is no jar any more,we need some hacky code.
     *     */
    public static void loadJogl(){
        try {
            //only graalvm support this method
            //disable hack when use other jre
            ImageInfo.inImageCode();
        }catch (NoClassDefFoundError e){
            return;
        }
        try {
            // loaded records what native libraries had loaded.
            // add library names to loaded that JOGL will not load any libraries
            Field loaded = JNILibLoaderBase.class.getDeclaredField("loaded");
            loaded.setAccessible(true);
            HashSet<String> o = (HashSet<String>) loaded.get(null);
            o.addAll(WINDOWS);

            WINDOWS.forEach((item)->{
                String path = "/natives/"
                       +System.mapLibraryName(item);
                try {
                    if (!ImageInfo.inImageCode() || !path.contains("nativewindow_awt")) {
                        NativeUtils.loadLibraryFromJar(path);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            });



        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("jogl native library load error");
        }
        loadSWT();
    }

    /**
     * just change SWT native directory to ours.
     */
    public static void loadSWT(){
        try {
            NativeUtils.loadLibraryFromJar("/natives/swt-wgl-win32-4332.dll");
            NativeUtils.loadLibraryFromJar("/natives/swt-win32-4332.dll");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.setProperty ("swt.library.path",NativeUtils.temporaryDir.getAbsolutePath());
    }
}
