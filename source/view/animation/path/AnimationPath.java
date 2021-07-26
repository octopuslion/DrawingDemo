package view.animation.path;

import java.awt.geom.Point2D;

public abstract class AnimationPath {
  public AnimationPath() {}

  public abstract Point2D.Double getBeginPoint();

  public abstract Point2D.Double getNextPoint(Point2D.Double sourcePoint);
}
