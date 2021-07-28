package Interpolator;

import java.awt.Color;
import java.awt.image.BufferedImage;

public abstract class ImageInterpolator {
  private final int targetWidth;
  private final int targetHeight;

  public ImageInterpolator(int targetWidth, int targetHeight) {
    // 目标图像的尺寸，大于原图为放大，小于原图为压缩。
    this.targetWidth = targetWidth;
    this.targetHeight = targetHeight;
  }

  public BufferedImage compress(BufferedImage sourceImage) {
    int radius = getRadius(); // 获得不同算法下需要获取的目标像素点四周关联像素半径。
    int sourceWidth = sourceImage.getWidth();
    int sourceHeight = sourceImage.getHeight();
    Color[][] sourceColors = getImageColors(sourceImage);
    Color[][] targetColors = new Color[targetHeight][targetWidth];
    int showWidth = Math.min(targetWidth, sourceWidth);
    int showHeight = Math.min(targetHeight, sourceHeight);

    // 将目标图像的像素点位置映射到原图的位置上，然后根据设定的半径来确定四周的像素点位置。
    for (int j = 0; j < showHeight; j++) {
      // 下边界容超出容错。
      double targetY = (double) j / targetHeight * sourceHeight;
      if (targetY >= sourceHeight - 1) {
        targetY = sourceHeight - 1;
      }

      // 上边界容超出容错。
      int sourceTopY = (int) targetY - radius + 1;
      if (sourceTopY < 0) {
        sourceTopY = 0;
      } else if (sourceTopY > sourceHeight - radius * 2) {
        sourceTopY = sourceHeight - radius * 2;
      }

      for (int i = 0; i < showWidth; i++) {
        // 右边界容超出容错。
        double targetX = (double) i / targetWidth * sourceWidth;
        if (targetX >= sourceWidth - 1) {
          targetX = sourceWidth - 1;
        }

        // 左边界容超出容错。
        int sourceLeftX = (int) targetX - radius + 1;
        if (sourceLeftX < 0) {
          sourceLeftX = 0;
        } else if (sourceLeftX > sourceWidth - radius * 2) {
          sourceLeftX = sourceWidth - radius * 2;
        }

        // 根据不同算法或者采样函数计算出目标位置的像素颜色值，视为一次值插入计算。
        targetColors[j][i] = getColor(targetX, targetY, sourceLeftX, sourceTopY, sourceColors);
      }
    }

    // 通过插值运算后的像素点颜色构造新的图像。
    BufferedImage targetImage =
        new BufferedImage(showWidth, showHeight, BufferedImage.TYPE_INT_ARGB);
    for (int j = 0; j < showHeight; j++) {
      for (int i = 0; i < showWidth; i++) {
        targetImage.setRGB(i, j, targetColors[j][i].getRGB());
      }
    }

    return targetImage;
  }

  protected abstract int getRadius();

  protected abstract Color getColor(
      double targetX, double targetY, int sourceLeftX, int sourceTopY, Color[][] sourceColors);

  protected double getDistance(double x1, double y1, double x2, double y2) {
    // 计算两点欧氏距离。
    return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
  }

  protected int toColorComponent(double value) {
    int validValue = (int) value;
    if (validValue < 0) {
      validValue = 0;
    } else if (validValue > 255) {
      validValue = 255;
    }

    return validValue;
  }

  private Color[][] getImageColors(BufferedImage image) {
    // 获取图像的所有像素点颜色。
    int width = image.getWidth();
    int height = image.getHeight();
    Color[][] colors = new Color[height][width];
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        int colorValue = image.getRGB(i, j);
        int alpha = colorValue >>> 24;
        int red = (colorValue >> 16) & 0xff;
        int green = (colorValue >> 8) & 0xff;
        int blue = colorValue & 0xff;
        colors[j][i] = new Color(red, green, blue, alpha);
      }
    }

    return colors;
  }
}
