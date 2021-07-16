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
import view.animation.BufferInterpolator;

public class AnimationPanel extends MainPanel implements Runnable {

  private static final long serialVersionUID = -4664401356270767037L;

  private final JButton toggleButton;
  private final JLabel captionLabel;
  private final int moveRadius;
  private final Point movePoint;
  private final int frameCount;
  private final BufferInterpolator interpolator;
  private byte playType;
  private Thread thread;
  private boolean stopThread;

  public AnimationPanel(Font componentFont) {
    super(componentFont);

    toggleButton = new JButton();
    captionLabel = new JLabel();
    moveRadius = 30; // 动画路径的半径。
    movePoint = new Point(-moveRadius, -moveRadius); // 动画路径平移点。
    interpolator = new BufferInterpolator(getBackground()); // 双缓存绘制器。
    playType = 1; // 播放状态标志，1=未使用双缓存，2=使用双缓存。
    frameCount = 24; // 帧数。
    thread = null; // 动画线程。
    stopThread = false; // 动画线程结束标志。
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

  public void togglePlayType() {
    if (playType == 1) {
      captionLabel.setText("使用双缓存");
      playType = 2;
    } else {
      captionLabel.setText("未使用双缓存");
      playType = 1;
    }

    updateUI();
  }

  public void animationStart() {
    // 启动动画线程和双缓存绘制器。
    interpolator.start();
    stopThread = false;
    if (thread == null) {
      thread = new Thread(this);
      thread.start();
    }
  }

  public void animationStop() {
    // 终止动画线程和双缓存绘制器。
    stopThread = true;
    interpolator.stop();
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
    if (playType == 1) {
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
      graphics2D.drawImage(interpolator.getBufferImage(), 0, 0, 500, 500, null);
    }
  }

  @Override
  public void run() {
    while (thread != null && !stopThread) {
      try {
        updateMovePoint();
        interpolator.update(movePoint);
        updateUI();

        // 根据帧数控制绘制间隔。
        thread.join(1000 / frameCount);
      } catch (InterruptedException ignored) {
      }
    }

    thread = null;
  }

  private void updateMovePoint() {
    // 更新绘制图形的平移位置，作正矩形路径平移。
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
  }
}
