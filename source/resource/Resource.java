package resource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class Resource {

  public static BufferedImage getPNG_SAMPLE_01() {
    // 获得sample_01.png。
    BufferedImage image = null;
    String path = getResourceRootPath();
    if (path != null) {
      path = Paths.get(path, "sample_01.png").toString();

      try {
        image = ImageIO.read(new File(path));
      } catch (IOException ignored) {
      }
    }

    if (image == null) {
      image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
    }

    return image;
  }

  private static String getResourceRootPath() {
    // 获得资源根目录。
    String path = null;
    String classFullName = Resource.class.getName();
    String className = classFullName.substring(classFullName.lastIndexOf('.') + 1);
    String classNameWithDotClass = className + ".class";
    URL url = Resource.class.getResource(classNameWithDotClass);
    if (url != null) {
      path = url.toString();
      path = path.substring(0, path.length() - classNameWithDotClass.length() - 1);
      path = path.replace("+", "<*^*>");
      path = URLDecoder.decode(path, StandardCharsets.UTF_8);
      path = path.replace("<*^*>", "+");
      String osName = System.getProperties().getProperty("os.name");
      if (osName.toLowerCase().startsWith("mac")) {
        path = path.substring(path.indexOf("file:") + 5);
      } else {
        path = path.substring(path.indexOf("file:/") + 6);
      }
      path = path.substring(0, path.lastIndexOf("/resource") + 1);
    }

    return path;
  }
}
