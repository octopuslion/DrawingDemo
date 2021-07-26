package view.animation.path;

import java.awt.geom.Point2D;

public class SquareAnimationPath extends AnimationPath {

  private final double radius;

  public SquareAnimationPath() {
    radius = 40;
  }

  @Override
  public Point2D.Double getBeginPoint() {
    return new Point2D.Double(-radius, -radius);
  }

  @Override
  public Point2D.Double getNextPoint(Point2D.Double sourcePoint) {
    // 得到下一个绘制图形的平移位置，作正矩形顺时针路径平移。
    Point2D.Double targetPoint = new Point2D.Double();
    if (Double.doubleToLongBits(sourcePoint.y) == Double.doubleToLongBits(-radius)) {
      if (Double.doubleToLongBits(sourcePoint.x) == Double.doubleToLongBits(radius)) {
        targetPoint.setLocation(sourcePoint.x, sourcePoint.y + 1);
      } else {
        targetPoint.setLocation(sourcePoint.x + 1, sourcePoint.y);
      }
    } else if (Double.doubleToLongBits(sourcePoint.x) == Double.doubleToLongBits(radius)) {
      if (Double.doubleToLongBits(sourcePoint.y) == Double.doubleToLongBits(radius)) {
        targetPoint.setLocation(sourcePoint.x - 1, sourcePoint.y);
      } else {
        targetPoint.setLocation(sourcePoint.x, sourcePoint.y + 1);
      }
    } else if (Double.doubleToLongBits(sourcePoint.y) == Double.doubleToLongBits(radius)) {
      if (Double.doubleToLongBits(sourcePoint.x) == Double.doubleToLongBits(-radius)) {
        targetPoint.setLocation(sourcePoint.x, sourcePoint.y - 1);
      } else {
        targetPoint.setLocation(sourcePoint.x - 1, sourcePoint.y);
      }
    } else if (Double.doubleToLongBits(sourcePoint.x) == Double.doubleToLongBits(-radius)) {
      targetPoint.setLocation(sourcePoint.x, sourcePoint.y - 1);
    } else {
      targetPoint.setLocation(sourcePoint.x, sourcePoint.y);
    }

    return targetPoint;
  }
}
