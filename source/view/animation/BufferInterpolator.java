package view.animation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class BufferInterpolator implements Runnable {
  private final Color backgroundColor;
  private final Image frontBufferImage;
  private final Image backBufferImage;
  private Point movePoint;
  private Thread thread;
  private boolean stopThread;

  public BufferInterpolator(Color backgroundColor) {
    this.backgroundColor = backgroundColor;

    // 前台缓存，会被绘制到控件上。
    frontBufferImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);

    // 后台缓存，在后台线程中阻塞绘制。
    backBufferImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);

    movePoint = null; // 动画路径平移点。
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

  public void update(Point movePoint) {
    if (this.movePoint == null) {
      this.movePoint = new Point(movePoint.x, movePoint.y);
    } else {
      this.movePoint.setLocation(movePoint.x, movePoint.y);
    }
  }

  @Override
  public void run() {
    while (thread != null && !stopThread) {
      if (movePoint == null) {
        // 如果当前没有路径平移点，则等待小段时间后再获取。
        try {
          thread.join(100);
        } catch (InterruptedException ignored) {
        }
      } else {
        drawBackBuffer(new Point(movePoint.x, movePoint.y));
        drawFrontBuffer();
      }
    }

    thread = null;
  }

  private void drawBackBuffer(Point movePoint) {
    // 绘制后台缓存。
    Graphics2D graphics2D = (Graphics2D) backBufferImage.getGraphics();
    graphics2D.setColor(backgroundColor);
    graphics2D.fillRect(0, 0, 500, 500);
    int[] polygonXPoints = new int[] {200, 130, 350, 350, 250, 300};
    int[] polygonYPoints = new int[] {50, 350, 350, 270, 270, 130};
    Polygon polygon = new Polygon(polygonXPoints, polygonYPoints, 6);
    polygon.translate(movePoint.x, movePoint.y);
    for (int i = 0; i < 100; i++) {
      graphics2D.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics2D.setColor(Color.decode("#4281ff"));
      graphics2D.fillPolygon(polygon);
      graphics2D.setColor(Color.decode("#074589"));
      graphics2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      graphics2D.drawPolygon(polygon);
    }
  }

  private void drawFrontBuffer() {
    // 绘制前台缓存。
    Graphics2D graphics2D = (Graphics2D) frontBufferImage.getGraphics();
    graphics2D.drawImage(backBufferImage, 0, 0, 500, 500, null);
  }
}
