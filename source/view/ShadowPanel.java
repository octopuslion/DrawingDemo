package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

public class ShadowPanel extends MainPanel {

  private static final long serialVersionUID = -875945574886254421L;
  private final Polygon polygon;

  public ShadowPanel(Font componentFont) {
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
    // 优先绘制阴影，再绘制原图。
    drawShadow(graphics2D, polygon);
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

  private void drawShadow(Graphics2D graphics2D, Polygon polygon) {
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    double radians = Math.toRadians(45); // 控制阴影角度，0-90度。
    double translateStepLength = 1; // 控制单层阴影的像素长度。
    int colorValue = 0; // 阴影的开始颜色，最低灰度值，0-255。
    int colorStepValue = 3; // 阴影的颜色增量，灰度逐渐变高，0为不变。
    int alphaValue = 0; // 阴影的开始透明度，最低透明度，0-255。
    int alphaStepValue = 3; // 阴影的透明度增量，透明度逐渐增大，0为不变。
    int step = 30; // 阴影增量，1为一层阴影。

    // 绘制顺序由远及近。
    for (int i = step - 1; i >= 0; i--) {
      // 计算得到此层阴影的偏移量。
      double translateLength = i * translateStepLength;
      int translateX = (int) (Math.cos(radians) * translateLength);
      int translateY = (int) (Math.sin(radians) * translateLength);

      // 绘制此层阴影。
      polygon.translate(translateX, translateY);
      graphics2D.setColor(new Color(colorValue, colorValue, colorValue, alphaValue));
      graphics2D.fillPolygon(polygon);
      polygon.translate(-translateX, -translateY);

      // 阴影颜色和透明度累增。
      colorValue += colorStepValue;
      alphaValue += alphaStepValue;
    }
  }
}
