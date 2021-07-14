package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import view.animation.FrameBuffer;

public class AnimationPanel extends MainPanel implements Runnable {

  private static final long serialVersionUID = -4664401356270767037L;

  private final JButton toggleButton;
  private final JLabel captionLabel;
  private final int moveRadius;
  private final Point movePoint;
  private final int frameCount;
  private final FrameBuffer frameBuffer;
  private byte toggleStatus;
  private Thread thread;
  private boolean stopThread;

  public AnimationPanel(Font componentFont) {
    super(componentFont);

    toggleButton = new JButton();
    captionLabel = new JLabel();
    toggleStatus = 1;
    moveRadius = 30;
    movePoint = new Point(-moveRadius, -moveRadius);
    frameCount = 24; // 帧数。
    thread = null;
    stopThread = false;
    frameBuffer = new FrameBuffer(getBackground()); // 双缓存绘制器。
  }

  @Override
  public void initialize(ActionListener actionListener) {
    super.initialize(actionListener);
    Font componentFont = getComponentFont();

    toggleButton.setName("AnimationPanel:ToggleButton");
    toggleButton.setBounds(5, 5, 40, 20);
    toggleButton.setFont(componentFont);
    toggleButton.setText("切换");
    toggleButton.addActionListener(actionListener);
    add(toggleButton);

    captionLabel.setBounds(50, 5, 100, 20);
    captionLabel.setFont(componentFont);
    captionLabel.setText("未使用双缓存");
    add(captionLabel);
  }

  public void statusToggle() {
    if (toggleStatus == 1) {
      captionLabel.setText("使用双缓存");
      toggleStatus = 2;
    } else {
      captionLabel.setText("未使用双缓存");
      toggleStatus = 1;
    }

    updateUI();
  }

  public void animationStart() {
    frameBuffer.start();
    stopThread = false;
    if (thread == null) {
      thread = new Thread(this);
      thread.start();
    }
  }

  public void animationStop() {
    stopThread = true;
    frameBuffer.stop();
  }

  @Override
  protected void drawLeftCanvas(Graphics2D graphics2D) {
    draw(graphics2D);
  }

  @Override
  protected void drawRightCanvas(Graphics2D graphics2D) {
    draw(graphics2D);
  }

  private void draw(Graphics2D graphics2D) {
    if (toggleStatus == 1) {
      // 未使用双缓存绘制。
      int[] polygonXPoints = new int[] {200, 130, 350, 350, 250, 300};
      int[] polygonYPoints = new int[] {50, 350, 350, 270, 270, 130};
      Polygon polygon = new Polygon(polygonXPoints, polygonYPoints, 6);
      polygon.translate(movePoint.x, movePoint.y);

      // 循环绘制100次制造绘制大量元素的情况。
      for (int i = 0; i < 100; i++) {
        graphics2D.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.decode("#4281ff"));
        graphics2D.fillPolygon(polygon);
        graphics2D.setColor(Color.decode("#074589"));
        graphics2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics2D.drawPolygon(polygon);
      }
    } else {
      // 使用双缓存绘制。
      graphics2D.drawImage(frameBuffer.getFrontBufferImage(), 0, 0, 500, 500, null);
    }
  }

  @Override
  public void run() {
    while (thread != null && !stopThread) {
      try {
        // 刷新绘制图形的位置。
        if (movePoint.y == -moveRadius) {
          if (movePoint.x == moveRadius) {
            movePoint.setLocation(movePoint.x, movePoint.y + 1);
          } else {
            movePoint.setLocation(movePoint.x + 1, movePoint.y);
          }
        } else if (movePoint.x == moveRadius) {
          if (movePoint.y == moveRadius) {
            movePoint.setLocation(movePoint.x - 1, movePoint.y);
          } else {
            movePoint.setLocation(movePoint.x, movePoint.y + 1);
          }
        } else if (movePoint.y == moveRadius) {
          if (movePoint.x == -moveRadius) {
            movePoint.setLocation(movePoint.x, movePoint.y - 1);
          } else {
            movePoint.setLocation(movePoint.x - 1, movePoint.y);
          }
        } else if (movePoint.x == -moveRadius) {
          movePoint.setLocation(movePoint.x, movePoint.y - 1);
        }

        frameBuffer.draw(movePoint);
        updateUI();

        // 根据帧数控制绘制间隔。
        thread.join(1000 / frameCount);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    thread = null;
  }
}
