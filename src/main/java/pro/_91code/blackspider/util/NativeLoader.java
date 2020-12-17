package pro._91code.blackspider.util;

import com.jogamp.common.jvm.JNILibLoaderBase;
import cz.adamh.utils.NativeUtils;
import org.graalvm.nativeimage.ImageInfo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;

public class NativeLoader {
    public static void loadJogl(){
        try {
            Field loaded = JNILibLoaderBase.class.getDeclaredField("loaded");
            loaded.setAccessible(true);
            HashSet<String> o = (HashSet<String>) loaded.get(null);
            o.add("gluegen-rt");
            o.add("jogl_desktop");
            o.add("jogl_mobile");
            o.add("nativewindow_awt");
            o.add("nativewindow_x11");
            NativeUtils.loadLibraryFromJar("/natives/linux-amd64/libgluegen-rt.so");
            NativeUtils.loadLibraryFromJar("/natives/linux-amd64/libjogl_desktop.so");
            NativeUtils.loadLibraryFromJar("/natives/linux-amd64/libjogl_mobile.so");
            NativeUtils.loadLibraryFromJar("/natives/linux-amd64/libnativewindow_x11.so");
            if (!ImageInfo.inImageCode()){
                NativeUtils.loadLibraryFromJar("/natives/linux-amd64/libnativewindow_awt.so");
            }
        } catch (NoSuchFieldException | IOException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("jogl native library load error");
        }
    }
}
