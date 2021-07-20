package view.animation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import view.animation.path.AnimationPath;

public class BufferInterpolator implements Runnable {
  private final Color backgroundColor;
  private final Image frontBufferImage;
  private final Image backBufferImage;
  private final AnimationPath animationPath;
  private Point2D.Double movePoint;
  private Thread thread;
  private boolean stopThread;

  public BufferInterpolator(Color backgroundColor, AnimationPath animationPath) {
    this.backgroundColor = backgroundColor;

    // 前台缓存，会被绘制到控件上。
    frontBufferImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);

    // 后台缓存，在后台线程中阻塞绘制。
    backBufferImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);

    this.animationPath = animationPath;
    movePoint = animationPath.getBeginPoint(); // 动画路径平移点。
    thread = null; // 后台绘制线程。
    stopThread = false; // 后台绘制线程结束标志。
  }

  public void start() {
    // 开启后台绘制线程。
    stopThread = false;
    if (thread == null) {
      thread = new Thread(this);
      thread.start();
    }
  }

  public void stop() {
    // 终止后台绘制线程。
    stopThread = true;
  }

  public Image getBufferImage() {
    return frontBufferImage;
  }

  public void update() {
    movePoint = animationPath.getNextPoint(movePoint);
  }

  @Override
  public void run() {
    while (thread != null && !stopThread) {
      // 绘制后台缓存。
      Graphics2D graphics2DForBackBuffer = (Graphics2D) backBufferImage.getGraphics();
      graphics2DForBackBuffer.setColor(backgroundColor);
      graphics2DForBackBuffer.fillRect(0, 0, 500, 500);
      drawContent(graphics2DForBackBuffer);

      // 绘制前台缓存。
      Graphics2D graphics2DForFrontBuffer = (Graphics2D) frontBufferImage.getGraphics();
      graphics2DForFrontBuffer.drawImage(backBufferImage, 0, 0, 500, 500, null);
    }

    thread = null;
  }

  public void drawContent(Graphics2D graphics2D) {
    int[] polygonXPoints = new int[] {200, 130, 350, 350, 250, 300};
    int[] polygonYPoints = new int[] {50, 350, 350, 270, 270, 130};
    Polygon polygon = new Polygon(polygonXPoints, polygonYPoints, 6);
    Point2D.Double nextMovePoint = new Point2D.Double(movePoint.x, movePoint.y);
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // 循环绘制100次制造绘制大量元素的情况。
    for (int i = 0; i < 100; i++) {
      polygon.translate((int) nextMovePoint.x, (int) nextMovePoint.y);
      graphics2D.setColor(Color.decode("#4281ff"));
      graphics2D.fillPolygon(polygon);
      graphics2D.setColor(Color.decode("#074589"));
      graphics2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      graphics2D.drawPolygon(polygon);
      polygon.translate(-(int) nextMovePoint.x, -(int) nextMovePoint.y);
      nextMovePoint = animationPath.getNextPoint(nextMovePoint);
    }
  }
}
