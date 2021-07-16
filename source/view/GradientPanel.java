package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class GradientPanel extends MainPanel {

  private static final long serialVersionUID = -6954413092538668285L;
  private final JLabel beginColorLabel;
  private final JComboBox<String> beginColorComboBox;
  private final JLabel endColorLabel;
  private final JComboBox<String> endColorComboBox;
  private final JLabel directionLabel;
  private final JComboBox<String> directionComboBox;

  public GradientPanel(Font componentFont) {
    super(componentFont);
    beginColorLabel = new JLabel();
    beginColorComboBox = new JComboBox<>();
    endColorLabel = new JLabel();
    endColorComboBox = new JComboBox<>();
    directionLabel = new JLabel();
    directionComboBox = new JComboBox<>();
  }

  @Override
  public void initialize(ActionListener actionListener) {
    super.initialize(actionListener);
    Font componentFont = getComponentFont();

    beginColorLabel.setBounds(5, 5, 100, 20);
    beginColorLabel.setFont(componentFont);
    beginColorLabel.setText("开始颜色：");
    add(beginColorLabel);

    beginColorComboBox.setBounds(70, 5, 70, 20);
    beginColorComboBox.setFont(componentFont);
    beginColorComboBox.addItem("红");
    beginColorComboBox.addItem("黄");
    beginColorComboBox.addItem("蓝");
    beginColorComboBox.setSelectedIndex(2);
    add(beginColorComboBox);

    endColorLabel.setBounds(145, 5, 100, 20);
    endColorLabel.setFont(componentFont);
    endColorLabel.setText("结束颜色：");
    add(endColorLabel);

    endColorComboBox.setBounds(210, 5, 70, 20);
    endColorComboBox.setFont(componentFont);
    endColorComboBox.addItem("红");
    endColorComboBox.addItem("黄");
    endColorComboBox.addItem("蓝");
    endColorComboBox.setSelectedIndex(1);
    add(endColorComboBox);

    directionLabel.setBounds(285, 5, 60, 20);
    directionLabel.setFont(componentFont);
    directionLabel.setText("方向：");
    add(directionLabel);

    directionComboBox.setBounds(320, 5, 120, 20);
    directionComboBox.setFont(componentFont);
    directionComboBox.addItem("上-下");
    directionComboBox.addItem("左-右");
    directionComboBox.addItem("左上-右下");
    directionComboBox.addItem("右上-左下");
    directionComboBox.setSelectedIndex(2);
    add(directionComboBox);
  }

  @Override
  protected void drawLeftCanvas(Graphics2D graphics2D) {
    // 绘制原始图形。
    Polygon polygon = createShape();
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setColor(Color.decode("#4281ff"));
    graphics2D.fillPolygon(polygon);
    graphics2D.setColor(Color.decode("#074589"));
    graphics2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    graphics2D.drawPolygon(polygon);
  }

  @Override
  protected void drawRightCanvas(Graphics2D graphics2D) {
    Polygon polygon = createShape();
    Color beginColor = getColor(beginColorComboBox); // 渐变起始颜色。
    Color endColor = getColor(endColorComboBox); // 渐变终止颜色。
    Point[] directionPoints = getDirectionPoints(polygon); // 绘制方向起止点，这个位置是相对于Graphics2D的。
    GradientPaint paint =
        new GradientPaint(
            directionPoints[0].x,
            directionPoints[0].y,
            beginColor,
            directionPoints[1].x,
            directionPoints[1].y,
            endColor,
            true); // 构造渐变刷。

    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setPaint(paint);
    graphics2D.fillPolygon(polygon);
    graphics2D.setColor(Color.decode("#074589"));
    graphics2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    graphics2D.drawPolygon(polygon);
  }

  private Polygon createShape() {
    int[] polygonXPoints = new int[] {200, 130, 350, 350, 250, 300};
    int[] polygonYPoints = new int[] {50, 350, 350, 270, 270, 130};
    return new Polygon(polygonXPoints, polygonYPoints, 6);
  }

  private Color getColor(JComboBox<String> colorComboBox) {
    if (colorComboBox.getSelectedIndex() == 0) {
      return Color.RED;
    } else if (colorComboBox.getSelectedIndex() == 1) {
      return Color.YELLOW;
    } else {
      return Color.BLUE;
    }
  }

  private Point[] getDirectionPoints(Polygon polygon) {
    Point fromPoint = new Point();
    Point toPoint = new Point();
    Rectangle bounds = polygon.getBounds();
    if (directionComboBox.getSelectedIndex() == 0) {
      // 从上往下，则起点在上边中点，终点在下边中点。
      fromPoint.setLocation(bounds.x + bounds.width / 2, bounds.y);
      toPoint.setLocation(fromPoint.x, bounds.y + bounds.height);
    } else if (directionComboBox.getSelectedIndex() == 1) {
      // 从左往右，则起点在左边中点，终点在右边中点。
      fromPoint.setLocation(bounds.x, bounds.y + bounds.height / 2);
      toPoint.setLocation(bounds.x + bounds.width, fromPoint.y);
    } else if (directionComboBox.getSelectedIndex() == 2) {
      // 左上到右下，则起点在左上点，终点在右下点。
      fromPoint.setLocation(bounds.x, bounds.y);
      toPoint.setLocation(bounds.x + bounds.width, bounds.y + bounds.height);
    } else {
      // 右上到左下，则起点在右上点，终点在左下点。
      fromPoint.setLocation(bounds.x + bounds.width, bounds.y);
      toPoint.setLocation(bounds.x, bounds.y + bounds.height);
    }

    return new Point[] {fromPoint, toPoint};
  }
}
