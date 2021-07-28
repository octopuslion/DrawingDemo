package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class GlowPanel extends MainPanel {

  private static final long serialVersionUID = -2392853781581527961L;
  private final JLabel colorLabel;
  private final JComboBox<String> colorComboBox;
  private final JLabel maxAlphaLabel;
  private final JTextField maxAlphaTextField;
  private final JLabel decreaseAlphaLabel;
  private final JTextField decreaseAlphaTextField;
  private final JLabel layerCountLabel;
  private final JTextField layerCountTextField;
  private final JLabel layerRadiusLabel;
  private final JTextField layerRadiusTextField;

  public GlowPanel(Font componentFont) {
    super(componentFont);
    colorLabel = new JLabel();
    colorComboBox = new JComboBox<>();
    maxAlphaLabel = new JLabel();
    maxAlphaTextField = new JTextField();
    decreaseAlphaLabel = new JLabel();
    decreaseAlphaTextField = new JTextField();
    layerCountLabel = new JLabel();
    layerCountTextField = new JTextField();
    layerRadiusLabel = new JLabel();
    layerRadiusTextField = new JTextField();
  }

  @Override
  public void initialize(ActionListener actionListener) {
    super.initialize(actionListener);
    Font componentFont = getComponentFont();

    colorLabel.setBounds(5, 5, 60, 20);
    colorLabel.setFont(componentFont);
    colorLabel.setText("颜色：");
    add(colorLabel);

    colorComboBox.setBounds(40, 5, 90, 20);
    colorComboBox.setFont(componentFont);
    colorComboBox.addItem("颜色1");
    colorComboBox.addItem("颜色2");
    colorComboBox.addItem("颜色3");
    colorComboBox.setSelectedIndex(0);
    add(colorComboBox);

    maxAlphaLabel.setBounds(135, 5, 100, 20);
    maxAlphaLabel.setFont(componentFont);
    maxAlphaLabel.setText("最大透明：");
    add(maxAlphaLabel);

    maxAlphaTextField.setBounds(200, 5, 40, 20);
    maxAlphaTextField.setFont(componentFont);
    maxAlphaTextField.setText("30");
    add(maxAlphaTextField);

    decreaseAlphaLabel.setBounds(245, 5, 100, 20);
    decreaseAlphaLabel.setFont(componentFont);
    decreaseAlphaLabel.setText("透明减量：");
    add(decreaseAlphaLabel);

    decreaseAlphaTextField.setBounds(310, 5, 40, 20);
    decreaseAlphaTextField.setFont(componentFont);
    decreaseAlphaTextField.setText("1");
    add(decreaseAlphaTextField);

    layerCountLabel.setBounds(355, 5, 60, 20);
    layerCountLabel.setFont(componentFont);
    layerCountLabel.setText("层数：");
    add(layerCountLabel);

    layerCountTextField.setBounds(390, 5, 40, 20);
    layerCountTextField.setFont(componentFont);
    layerCountTextField.setText("30");
    add(layerCountTextField);

    layerRadiusLabel.setBounds(435, 5, 100, 20);
    layerRadiusLabel.setFont(componentFont);
    layerRadiusLabel.setText("单层半径：");
    add(layerRadiusLabel);

    layerRadiusTextField.setBounds(500, 5, 40, 20);
    layerRadiusTextField.setFont(componentFont);
    layerRadiusTextField.setText("1");
    add(layerRadiusTextField);
  }

  @Override
  protected void drawLeftCanvas(Graphics2D graphics2D) {
    Polygon polygon = createShape();
    drawPlain(graphics2D, polygon);
  }

  @Override
  protected void drawRightCanvas(Graphics2D graphics2D) {
    // 优先绘制发光，再绘制原图。
    Polygon polygon = createShape();
    drawGlow(graphics2D, polygon);
    drawPlain(graphics2D, polygon);
  }

  private Polygon createShape() {
    int[] polygonXPoints = new int[] {200, 100, 550, 550, 330, 400};
    int[] polygonYPoints = new int[] {100, 600, 600, 470, 470, 230};
    return new Polygon(polygonXPoints, polygonYPoints, 6);
  }

  private Color getColor() {
    if (colorComboBox.getSelectedIndex() == 0) {
      return Color.decode("#00a7ff");
    } else if (colorComboBox.getSelectedIndex() == 1) {
      return Color.decode("#75c44c");
    } else {
      return Color.decode("#ff993c");
    }
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
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setColor(Color.decode("#4281ff"));
    graphics2D.fillPolygon(polygon);
    graphics2D.setColor(Color.decode("#074589"));
    graphics2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    graphics2D.drawPolygon(polygon);
  }

  private void drawGlow(Graphics2D graphics2D, Polygon polygon) {
    // 绘制发光效果。
    Color glowColor = getColor(); // 光罩颜色。
    int maxAlpha = getValue(maxAlphaTextField, 0, 255); // 光罩的开始透明度，最高透明度，0-255。
    int decreaseAlpha = getValue(decreaseAlphaTextField, 0, 255); // 光罩的透明度减量，透明度逐渐减小，0为不变。
    int layerCount = getValue(layerCountTextField, 0, 100); // 光罩增量，1为一层光罩。
    int layerRadius = getValue(layerRadiusTextField, 1, 500); // 控制单层光罩的像素长度。

    // 绘制顺序由远及近。
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    for (int i = layerCount; i >= 1; i--) {
      // 计算得到此层光罩的绘制图形。
      Polygon expandPolygon = expand(polygon, layerRadius * i);

      // 计算得到此层阴影透明度。
      int alphaValue = maxAlpha - decreaseAlpha * (i - 1);
      if (alphaValue < 0) {
        alphaValue = 0;
      }

      // 绘制此层光罩。
      graphics2D.setColor(
          new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), alphaValue));
      graphics2D.fillPolygon(expandPolygon);
    }
  }

  private Polygon expand(Polygon polygon, double difference) {
    // 折线平行线算法，原理为通过顶点两边延长线来构造平行四边形，通过向量计算得到扩展或缩放距离。
    Point2D[] vectorDiffs = new Point2D[polygon.npoints];
    for (int i = 0; i < polygon.npoints; i++) {
      Point vertex1 = new Point(polygon.xpoints[i], polygon.ypoints[i]);
      int nextIndex = i == polygon.npoints - 1 ? 0 : i + 1;
      Point vertex2 = new Point(polygon.xpoints[nextIndex], polygon.ypoints[nextIndex]);
      Point vectorDiff = new Point(vertex2.x - vertex1.x, vertex2.y - vertex1.y);
      double factor = 1 / Math.sqrt(vectorDiff.x * vectorDiff.x + vectorDiff.y * vectorDiff.y);
      vectorDiffs[i] = new Point2D.Double(vectorDiff.x * factor, vectorDiff.y * factor);
    }

    int[] polygonXPoints = new int[polygon.npoints];
    int[] polygonYPoints = new int[polygon.npoints];
    for (int i = 0; i < polygon.npoints; i++) {
      int previousIndex = i == 0 ? polygon.npoints - 1 : i - 1;
      double x1 = vectorDiffs[previousIndex].getX();
      double y1 = vectorDiffs[previousIndex].getY();
      double x2 = vectorDiffs[i].getX();
      double y2 = vectorDiffs[i].getY();
      double length = difference / (x1 * y2 - y1 * x2);
      int vectorX = (int) ((x2 - x1) * length);
      int vectorY = (int) ((y2 - y1) * length);
      polygonXPoints[i] = polygon.xpoints[i] + vectorX;
      polygonYPoints[i] = polygon.ypoints[i] + vectorY;
    }

    return new Polygon(polygonXPoints, polygonYPoints, polygon.npoints);
  }
}
