package view.animation.path;

import java.awt.geom.Point2D;

public class CircleAnimationPath extends AnimationPath {

  private final int radius;

  public CircleAnimationPath() {
    radius = 40;
  }

  @Override
  public Point2D.Double getBeginPoint() {
    return new Point2D.Double(0, -radius);
  }

  @Override
  public Point2D.Double getNextPoint(Point2D.Double sourcePoint) {
    // 平面坐标转极坐标需要注意y轴需要反向。
    long sourceY = Double.doubleToLongBits(-sourcePoint.y);
    double sourceRadians;
    if (sourceY >= 0) {
      sourceRadians = Math.acos(sourcePoint.x / radius);
    } else {
      sourceRadians = Math.PI * 2 - Math.acos(sourcePoint.x / radius);
    }

    // 进行位置偏移。
    double incrementRadians = Math.asin(0.5 / radius) * 2;
    double targetRadians = sourceRadians - incrementRadians;
    if (targetRadians < 0) {
      targetRadians += Math.PI * 2;
    }

    // 极坐标转换回平面坐标。
    double nextX = Math.cos(targetRadians) * radius;
    double nextY = Math.sin(targetRadians) * radius;
    return new Point2D.Double(nextX, -nextY);
  }
}
