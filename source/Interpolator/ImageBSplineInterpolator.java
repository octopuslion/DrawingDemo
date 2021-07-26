package Interpolator;

public class ImageBSplineInterpolator extends ImageBicubicInterpolator {

  public ImageBSplineInterpolator(int targetWidth, int targetHeight) {
    super(targetWidth, targetHeight);
  }

  @Override
  protected double getYByFunction(double x) {
    // B样条函数的基函数计算过程。
    double absoluteX = Math.abs(x);
    if (absoluteX <= 1) {
      return 2.0 / 3 + 0.5 * Math.pow(absoluteX, 3) - Math.pow(x, 2);
    } else if (absoluteX <= 2) {
      return 1.0 / 6 * Math.pow(2 - absoluteX, 3);
    } else {
      return 0;
    }
  }
}
