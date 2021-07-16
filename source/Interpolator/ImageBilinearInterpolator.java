package Interpolator;

import java.awt.Color;

public class ImageBilinearInterpolator extends ImageInterpolator {

  public ImageBilinearInterpolator() {}

  @Override
  protected int getRadius() {
    // 双线性插值搜索半径为1。
    return 1;
  }

  @Override
  protected Color getColor(
      double targetX, double targetY, int sourceLeftX, int sourceTopY, Color[][] sourceColors) {
    // 步骤1：计算上方两个原图坐标点之间与目标点垂直方向上的插值颜色，采用线性采样。
    Color targetTopColor =
        getColorByFunction(
            sourceLeftX,
            sourceColors[sourceLeftX][sourceTopY],
            sourceLeftX + 1,
            sourceColors[sourceLeftX + 1][sourceTopY],
            targetX);

    // 步骤2：计算下方两个原图坐标点之间与目标点垂直方向上的插值颜色，采用线性采样。
    Color targetBottomColor =
        getColorByFunction(
            sourceLeftX,
            sourceColors[sourceLeftX][sourceTopY + 1],
            sourceLeftX + 1,
            sourceColors[sourceLeftX + 1][sourceTopY + 1],
            targetX);

    // 步骤3：根据上下采样颜色以及位置来计算目标位置的插值颜色，此为在采样基础上再进行采样，故为二次采样，采用线性采样。
    return getColorByFunction(
        sourceTopY, targetTopColor, sourceTopY + 1, targetBottomColor, targetY);
  }

  private Color getColorByFunction(
      // 分别对3通道颜色以及透明度通道进行线性采样计算插值。
      double sourceX1, Color sourceY1, double sourceX2, Color sourceY2, double targetX) {
    double targetYRed =
        getYByFunction(sourceX1, sourceY1.getRed(), sourceX2, sourceY2.getRed(), targetX);
    double targetYGreen =
        getYByFunction(sourceX1, sourceY1.getGreen(), sourceX2, sourceY2.getGreen(), targetX);
    double targetYBlue =
        getYByFunction(sourceX1, sourceY1.getBlue(), sourceX2, sourceY2.getBlue(), targetX);
    double targetYAlpha =
        getYByFunction(sourceX1, sourceY1.getAlpha(), sourceX2, sourceY2.getAlpha(), targetX);

    // 根据4个通道的采样值构造插值颜色。
    return new Color(
        toColorComponent(targetYRed),
        toColorComponent(targetYGreen),
        toColorComponent(targetYBlue),
        toColorComponent(targetYAlpha));
  }

  private double getYByFunction(
      double sourceX1, double sourceY1, double sourceX2, double sourceY2, double targetX) {
    // 为二元一次方程构造并求解，如果横纵坐标分别相同，则直接返回结果。
    if (Math.abs(sourceY1 - sourceY2) <= Double.MIN_NORMAL
        || Math.abs(sourceX1 - sourceX2) <= Double.MIN_NORMAL) {
      return sourceY1;
    }

    // 分别计算斜率和偏移，而后根据x求得y。
    double slop = (sourceY1 - sourceY2) / (sourceX1 - sourceX2);
    double offset = sourceY1 - slop * sourceX1;
    return slop * targetX + offset;
  }
}
