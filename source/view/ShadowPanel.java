package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ShadowPanel extends MainPanel {

  private static final long serialVersionUID = -875945574886254421L;
  private final JLabel minGrayLabel;
  private final JTextField minGrayTextField;
  private final JLabel incrementGrayLabel;
  private final JTextField incrementGrayTextField;
  private final JLabel maxAlphaLabel;
  private final JTextField maxAlphaTextField;
  private final JLabel decreaseAlphaLabel;
  private final JTextField decreaseAlphaTextField;
  private final JLabel layerCountLabel;
  private final JTextField layerCountTextField;
  private final JLabel layerRadiusLabel;
  private final JTextField layerRadiusTextField;
  private final JLabel degreeLabel;
  private final JTextField degreeTextField;

  public ShadowPanel(Font componentFont) {
    super(componentFont);
    minGrayLabel = new JLabel();
    minGrayTextField = new JTextField();
    incrementGrayLabel = new JLabel();
    incrementGrayTextField = new JTextField();
    maxAlphaLabel = new JLabel();
    maxAlphaTextField = new JTextField();
    decreaseAlphaLabel = new JLabel();
    decreaseAlphaTextField = new JTextField();
    layerCountLabel = new JLabel();
    layerCountTextField = new JTextField();
    layerRadiusLabel = new JLabel();
    layerRadiusTextField = new JTextField();
    degreeLabel = new JLabel();
    degreeTextField = new JTextField();
  }

  @Override
  public void initialize(ActionListener actionListener) {
    super.initialize(actionListener);
    Font componentFont = getComponentFont();

    minGrayLabel.setBounds(5, 5, 100, 20);
    minGrayLabel.setFont(componentFont);
    minGrayLabel.setText("最小灰度：");
    add(minGrayLabel);

    minGrayTextField.setBounds(70, 5, 40, 20);
    minGrayTextField.setFont(componentFont);
    minGrayTextField.setText("50");
    add(minGrayTextField);

    incrementGrayLabel.setBounds(115, 5, 100, 20);
    incrementGrayLabel.setFont(componentFont);
    incrementGrayLabel.setText("灰度增量：");
    add(incrementGrayLabel);

    incrementGrayTextField.setBounds(180, 5, 40, 20);
    incrementGrayTextField.setFont(componentFont);
    incrementGrayTextField.setText("3");
    add(incrementGrayTextField);

    maxAlphaLabel.setBounds(225, 5, 100, 20);
    maxAlphaLabel.setFont(componentFont);
    maxAlphaLabel.setText("最大透明：");
    add(maxAlphaLabel);

    maxAlphaTextField.setBounds(290, 5, 40, 20);
    maxAlphaTextField.setFont(componentFont);
    maxAlphaTextField.setText("100");
    add(maxAlphaTextField);

    decreaseAlphaLabel.setBounds(335, 5, 100, 20);
    decreaseAlphaLabel.setFont(componentFont);
    decreaseAlphaLabel.setText("透明减量：");
    add(decreaseAlphaLabel);

    decreaseAlphaTextField.setBounds(400, 5, 40, 20);
    decreaseAlphaTextField.setFont(componentFont);
    decreaseAlphaTextField.setText("3");
    add(decreaseAlphaTextField);

    layerCountLabel.setBounds(445, 5, 60, 20);
    layerCountLabel.setFont(componentFont);
    layerCountLabel.setText("层数：");
    add(layerCountLabel);

    layerCountTextField.setBounds(480, 5, 40, 20);
    layerCountTextField.setFont(componentFont);
    layerCountTextField.setText("30");
    add(layerCountTextField);

    layerRadiusLabel.setBounds(525, 5, 100, 20);
    layerRadiusLabel.setFont(componentFont);
    layerRadiusLabel.setText("单层半径：");
    add(layerRadiusLabel);

    layerRadiusTextField.setBounds(590, 5, 40, 20);
    layerRadiusTextField.setFont(componentFont);
    layerRadiusTextField.setText("1");
    add(layerRadiusTextField);

    degreeLabel.setBounds(635, 5, 60, 20);
    degreeLabel.setFont(componentFont);
    degreeLabel.setText("角度：");
    add(degreeLabel);

    degreeTextField.setBounds(670, 5, 40, 20);
    degreeTextField.setFont(componentFont);
    degreeTextField.setText("45");
    add(degreeTextField);
  }

  @Override
  protected void drawLeftCanvas(Graphics2D graphics2D) {
    Polygon polygon = createShape();
    drawPlain(graphics2D, polygon);
  }

  @Override
  protected void drawRightCanvas(Graphics2D graphics2D) {
    // 优先绘制阴影，再绘制原图。
    Polygon polygon = createShape();
    drawShadow(graphics2D, polygon);
    drawPlain(graphics2D, polygon);
  }

  private Polygon createShape() {
    int[] polygonXPoints = new int[] {200, 130, 350, 350, 250, 300};
    int[] polygonYPoints = new int[] {50, 350, 350, 270, 270, 130};
    return new Polygon(polygonXPoints, polygonYPoints, 6);
  }

  private int getValue(JTextField textField, int min, int max) {
    int value = min;

    try {
      value = Integer.parseInt(textField.getText());
    } catch (NumberFormatException ignored) {
    }

    if (value < min) {
      value = min;
    } else if (value > max) {
      value = max;
    }

    return value;
  }

  private void drawPlain(Graphics2D graphics2D, Polygon polygon) {
    // 绘制原始图形。
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setColor(Color.decode("#4281ff"));
    graphics2D.fillPolygon(polygon);
    graphics2D.setColor(Color.decode("#074589"));
    graphics2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    graphics2D.drawPolygon(polygon);
  }

  private void drawShadow(Graphics2D graphics2D, Polygon polygon) {
    // 绘制阴影。
    int minGray = getValue(minGrayTextField, 0, 255); // 阴影的开始颜色，最低灰度值，0-255。
    int incrementGray = getValue(incrementGrayTextField, 0, 255); // 阴影的颜色增量，灰度逐渐变高，0为不变。
    int maxAlpha = getValue(maxAlphaTextField, 0, 255); // 阴影的开始透明度，最高透明度，0-255。
    int decreaseAlpha = getValue(decreaseAlphaTextField, 0, 255); // 阴影的透明度减量，透明度逐渐减小，0为不变。
    int layerCount = getValue(layerCountTextField, 1, 100); // 阴影层数，1为一层阴影。
    int layerRadius = getValue(layerRadiusTextField, 1, 500); // 控制单层阴影的像素长度。
    double radians = Math.toRadians(getValue(degreeTextField, 0, 90)); // 控制阴影角度。

    // 绘制顺序由远及近。
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    for (int i = layerCount; i >= 1; i--) {
      // 计算得到此层阴影的偏移量。
      double translateLength = i * layerRadius;
      int translateX = (int) (Math.cos(radians) * translateLength);
      int translateY = (int) (Math.sin(radians) * translateLength);

      // 计算得到此层阴影灰度。
      int colorValue = minGray + incrementGray * (i - 1);
      if (colorValue > 255) {
        colorValue = 255;
      }

      // 计算得到此层阴影透明度。
      int alphaValue = maxAlpha - decreaseAlpha * (i - 1);
      if (alphaValue < 0) {
        alphaValue = 0;
      }

      // 绘制此层阴影。
      polygon.translate(translateX, translateY);
      graphics2D.setColor(new Color(colorValue, colorValue, colorValue, alphaValue));
      graphics2D.fillPolygon(polygon);
      polygon.translate(-translateX, -translateY);
    }
  }
}
