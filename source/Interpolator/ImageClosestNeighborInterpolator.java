package Interpolator;

import java.awt.Color;

public class ImageClosestNeighborInterpolator extends ImageInterpolator {

  public ImageClosestNeighborInterpolator() {}

  @Override
  public int getRadius() {
    // 最近邻算法搜索半径为1。
    return 1;
  }

  @Override
  public Color getColor(
      double targetX, double targetY, int sourceLeftX, int sourceTopY, Color[][] sourceColors) {
    // 通过计算目标点和其四周4个点的距离，来使用距离最近点的像素颜色作为目标颜色。
    double minDistance = Double.MAX_VALUE;
    int matchedSourceX = sourceLeftX;
    int matchedSourceY = sourceTopY;
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        int sourceX = sourceLeftX + i;
        int sourceY = sourceTopY + j;
        double distance = getDistance(targetX, targetY, sourceX, sourceY);
        if (distance < minDistance) {
          minDistance = distance;
          matchedSourceX = sourceX;
          matchedSourceY = sourceY;
        }
      }
    }

    return sourceColors[matchedSourceX][matchedSourceY];
  }
}
