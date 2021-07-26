package Interpolator;

import java.awt.Color;

public abstract class ImageBicubicInterpolator extends ImageInterpolator {

  public ImageBicubicInterpolator(int targetWidth, int targetHeight) {
    super(targetWidth, targetHeight);
  }

  @Override
  protected int getRadius() {
    // 双三次插值搜索半径为2。
    return 2;
  }

  @Override
  protected Color getColor(
      double targetX, double targetY, int sourceLeftX, int sourceTopY, Color[][] sourceColors) {
    // 步骤1：分别计算水平和垂直方向上的点距离，带入基函数得到两个分量权重，取乘积为该原图点颜色权重值。
    double[][] weights = new double[4][4];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        weights[i][j] =
            getYByFunction(Math.abs(sourceLeftX + i - targetX))
                * getYByFunction(Math.abs(sourceTopY + j - targetY));
      }
    }

    // 步骤2：权重值进行归一化处理，防止权重溢出，继而造成颜色通道值溢出。
    weights = getNormalizedWeights(weights);

    // 步骤3：通过权重值与4通道点像素颜色值的乘积累加，得到最终目标点的4通道颜色值。这里反映了目标点四周原图点对其的影响。
    double red = 0;
    double green = 0;
    double blue = 0;
    double alpha = 0;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        Color sourceColor = sourceColors[sourceLeftX + i][sourceTopY + j];
        red += sourceColor.getRed() * weights[i][j];
        green += sourceColor.getGreen() * weights[i][j];
        blue += sourceColor.getBlue() * weights[i][j];
        alpha += sourceColor.getAlpha() * weights[i][j];
      }
    }

    // 根据4个通道的采样值构造插值颜色。
    return new Color(
        toColorComponent(red),
        toColorComponent(green),
        toColorComponent(blue),
        toColorComponent(alpha));
  }

  protected abstract double getYByFunction(double x);

  private double[][] getNormalizedWeights(double[][] sourceWeights) {
    // 求得权重系数总和。
    double sum = 0;
    for (int i = 0; i < sourceWeights[0].length; i++) {
      for (int j = 0; j < sourceWeights.length; j++) {
        sum += sourceWeights[i][j];
      }
    }

    double[][] targetWeights = new double[sourceWeights[0].length][sourceWeights.length];
    for (int i = 0; i < sourceWeights[0].length; i++) {
      for (int j = 0; j < sourceWeights.length; j++) {
        if (sum < 0) {
          // 若总系数和小于0，则说明此权重系数矩阵的影响力为0，则所有系数均置为0。
          targetWeights[i][j] = 0;
        } else if (sum > 0) {
          // 否则需要调整所有权重系数的比例。
          targetWeights[i][j] = sourceWeights[i][j] / sum;
        }
      }
    }

    return targetWeights;
  }
}
