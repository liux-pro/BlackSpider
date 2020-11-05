package org.libjpegturbo.turbojpeg;

import cz.adamh.utils.NativeUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

final class TJLoader {
  static void load() {
//    try {
//      String libraryName = System.mapLibraryName("turbojpeg");
//      URL resource = TJLoader.class.getClassLoader().getResource(libraryName);
//      System.load(new File(resource.toURI()).toString());
//    }catch (URISyntaxException e){
//      System.out.println(e);
//    }
    try {
      String libraryName = System.mapLibraryName("turbojpeg");
      NativeUtils.loadLibraryFromJar("/"+libraryName);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
