package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;

public class GradientPanel extends MainPanel {

  private static final long serialVersionUID = -6954413092538668285L;
  private final Polygon polygon;

  public GradientPanel(Font componentFont) {
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
    drawGradient(graphics2D, polygon);
  }

  private void draw(Graphics2D graphics2D, Polygon polygon) {
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setColor(Color.decode("#4281ff"));
    graphics2D.fillPolygon(polygon);
    graphics2D.setColor(Color.decode("#074589"));
    graphics2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    graphics2D.drawPolygon(polygon);
  }

  private void drawGradient(Graphics2D graphics2D, Polygon polygon) {
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Rectangle polygonBounds = polygon.getBounds();
    Color beginColor = Color.BLUE; // 渐变起始颜色。
    float beginX = polygonBounds.x; // 渐变起始点X，这个位置是相对于Graphics2D的。
    float beginY = polygonBounds.y; // 渐变起始点Y。
    Color endColor = Color.GREEN; // 渐变终止颜色。
    float endX = polygonBounds.x + polygonBounds.width; // 渐变终止点X。
    float endY = polygonBounds.y + polygonBounds.height; // 渐变终止点Y。
    GradientPaint paint = new GradientPaint(beginX, beginY, beginColor, endX, endY, endColor, true);

    graphics2D.setPaint(paint);
    graphics2D.fillPolygon(polygon);
    graphics2D.setColor(Color.decode("#074589"));
    graphics2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    graphics2D.drawPolygon(polygon);
  }
}
