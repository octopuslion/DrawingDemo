package view;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import view.animation.BufferInterpolator;
import view.animation.path.CircleAnimationPath;
import view.animation.path.SquareAnimationPath;

public class AnimationPanel extends MainPanel implements Runnable {

  private static final long serialVersionUID = -4664401356270767037L;
  private final JLabel frameCountLabel;
  private final JTextField frameCountTextField;
  private final JLabel pathLabel;
  private final JComboBox<String> pathComboBox;
  private final JLabel bufferLabel;
  private final JComboBox<String> bufferComboBox;
  private final JButton runButton;
  private BufferInterpolator interpolator;
  private Thread thread;
  private boolean stopThread;

  public AnimationPanel(Font componentFont) {
    super(componentFont);
    frameCountLabel = new JLabel();
    frameCountTextField = new JTextField();
    pathLabel = new JLabel();
    pathComboBox = new JComboBox<>();
    bufferLabel = new JLabel();
    bufferComboBox = new JComboBox<>();
    runButton = new JButton();
    interpolator = new BufferInterpolator(getBackground(), new SquareAnimationPath()); // 双缓存绘制器。
    thread = null; // 动画线程。
    stopThread = false; // 动画线程结束标志。
  }

  @Override
  public void initialize(ActionListener actionListener) {
    super.initialize(actionListener);
    Font componentFont = getComponentFont();

    frameCountLabel.setBounds(5, 5, 100, 20);
    frameCountLabel.setFont(componentFont);
    frameCountLabel.setText("每秒刷新：");
    add(frameCountLabel);

    frameCountTextField.setBounds(70, 5, 40, 20);
    frameCountTextField.setFont(componentFont);
    frameCountTextField.setText("24");
    add(frameCountTextField);

    pathLabel.setBounds(115, 5, 100, 20);
    pathLabel.setFont(componentFont);
    pathLabel.setText("动画路径：");
    add(pathLabel);

    pathComboBox.setBounds(180, 5, 100, 20);
    pathComboBox.setFont(componentFont);
    pathComboBox.addItem("正方形");
    pathComboBox.addItem("圆形");
    pathComboBox.setSelectedIndex(0);
    add(pathComboBox);

    bufferLabel.setBounds(285, 5, 60, 20);
    bufferLabel.setFont(componentFont);
    bufferLabel.setText("双缓存：");
    add(bufferLabel);

    bufferComboBox.setBounds(335, 5, 100, 20);
    bufferComboBox.setFont(componentFont);
    bufferComboBox.addItem("未使用");
    bufferComboBox.addItem("使用");
    bufferComboBox.setSelectedIndex(0);
    add(bufferComboBox);

    runButton.setName("AnimationPanel:RunButton");
    runButton.setBounds(440, 5, 40, 20);
    runButton.setFont(componentFont);
    runButton.setText("开始");
    runButton.addActionListener(actionListener);
    add(runButton);
  }

  public void togglePlayType() {
    if (runButton.getText().equals("开始")) {
      // 更新控件状态。
      runButton.setText("结束");
      frameCountTextField.setEnabled(false);
      pathComboBox.setEnabled(false);
      bufferComboBox.setEnabled(false);
      interpolator = getBufferInterpolator();

      // 启动动画线程和双缓存绘制器。
      interpolator.start();
      stopThread = false;
      if (thread == null) {
        thread = new Thread(this);
        thread.start();
      }
    } else {
      // 终止动画线程和双缓存绘制器。
      stopThread = true;
      interpolator.stop();

      // 更新控件状态。
      runButton.setText("开始");
      frameCountTextField.setEnabled(true);
      pathComboBox.setEnabled(true);
      bufferComboBox.setEnabled(true);
    }
  }

  public void resetPlayType() {
    // 终止动画线程和双缓存绘制器。
    stopThread = true;
    interpolator.stop();

    // 重制控件状态。
    runButton.setText("开始");
    frameCountTextField.setEnabled(true);
    pathComboBox.setEnabled(true);
    bufferComboBox.setEnabled(true);
  }

  @Override
  protected void drawLeftCanvas(Graphics2D graphics2D) {
    draw(graphics2D);
  }

  @Override
  protected void drawRightCanvas(Graphics2D graphics2D) {
    draw(graphics2D);
  }

  @Override
  public void run() {
    while (thread != null && !stopThread) {
      try {
        interpolator.update();
        updateUI();

        // 根据帧数控制绘制间隔。
        thread.join(1000 / getFrameCount());
      } catch (InterruptedException ignored) {
      }
    }

    thread = null;
  }

  private int getFrameCount() {
    int min = 1;
    int max = 200;
    int value = min;

    try {
      value = Integer.parseInt(frameCountTextField.getText());
    } catch (NumberFormatException ignored) {
    }

    if (value < min) {
      value = min;
    } else if (value > max) {
      value = max;
    }

    return value;
  }

  private BufferInterpolator getBufferInterpolator() {
    if (pathComboBox.getSelectedIndex() == 0) {
      return new BufferInterpolator(getBackground(), new SquareAnimationPath());
    } else {
      return new BufferInterpolator(getBackground(), new CircleAnimationPath());
    }
  }

  private void draw(Graphics2D graphics2D) {
    if (thread == null) {
      return;
    }

    if (bufferComboBox.getSelectedIndex() == 0) {
      // 未使用双缓存绘制。
      interpolator.drawContent(graphics2D);
    } else {
      // 使用双缓存绘制。
      graphics2D.drawImage(interpolator.getBufferImage(), 0, 0, 500, 500, null);
    }
  }
}
