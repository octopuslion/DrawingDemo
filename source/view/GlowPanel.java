package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

public class GlowPanel extends MainPanel {

  private static final long serialVersionUID = -2392853781581527961L;
  private final Polygon polygon;

  public GlowPanel(Font componentFont) {
    super(componentFont);
    int[] polygonXPoints = new int[] {200, 130, 350, 350, 250, 300};
    int[] polygonYPoints = new int[] {50, 350, 350, 270, 270, 130};
    polygon = new Polygon(polygonXPoints, polygonYPoints, 6);
  }

  @Override
  protected void drawLeftCanvas(Graphics2D graphics2D) {
    draw(graphics2D, polygon);
  }

  @Override
  protected void drawRightCanvas(Graphics2D graphics2D) {
    drawGlow(graphics2D, polygon);
    draw(graphics2D, polygon);
  }

  private void draw(Graphics2D graphics2D, Polygon polygon) {
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setColor(Color.decode("#4281ff"));
    graphics2D.fillPolygon(polygon);
    graphics2D.setColor(Color.decode("#074589"));
    graphics2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    graphics2D.drawPolygon(polygon);
  }

  private void drawGlow(Graphics2D graphics2D, Polygon polygon) {
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Color glowColor = Color.decode("#00a7ff"); // 光罩颜色。
    double expandStepLength = 1; // 控制单层光罩的像素长度。
    int alphaValue = 0; // 光罩的开始透明度，最低透明度，0-255。
    int alphaStepValue = 1; // 光罩的透明度增量，透明度逐渐增大，0为不变。
    int step = 30; // 光罩增量，1为一层光罩。

    // 绘制顺序由远及近。
    for (int i = step - 1; i >= 0; i--) {
      // 计算得到此层光罩的绘制图形。
      Polygon expandPolygon = expand(polygon, expandStepLength * i);

      // 绘制此层光罩。
      graphics2D.setColor(
          new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), alphaValue));
      graphics2D.fillPolygon(expandPolygon);

      // 光罩透明度累增。
      alphaValue += alphaStepValue;
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
