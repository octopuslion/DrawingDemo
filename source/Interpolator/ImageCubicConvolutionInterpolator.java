package Interpolator;

public class ImageCubicConvolutionInterpolator extends ImageBicubicInterpolator {

  private final double parameterA;

  public ImageCubicConvolutionInterpolator(int targetWidth, int targetHeight) {
    super(targetWidth, targetHeight);
    parameterA = -1;
  }

  @Override
  protected double getYByFunction(double x) {
    // 三次卷积的基函数计算过程。
    double absoluteX = Math.abs(x);
    if (absoluteX <= 1) {
      return (parameterA + 2) * Math.pow(absoluteX, 3)
          - (parameterA + 3) * Math.pow(absoluteX, 2)
          + 1;
    } else if (absoluteX < 2) {
      return parameterA * Math.pow(absoluteX, 3)
          - 5 * parameterA * Math.pow(absoluteX, 2)
          + 8 * parameterA * absoluteX
          - 4 * parameterA;
    } else {
      return 0;
    }
  }
}
