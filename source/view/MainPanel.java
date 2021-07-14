package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;

public abstract class MainPanel extends JPanel {

  private static final long serialVersionUID = -3406895636400800696L;
  private final Font componentFont;

  public MainPanel(Font componentFont) {
    this.componentFont = componentFont;
  }

  protected Font getComponentFont() {
    return componentFont;
  }

  public void initialize(ActionListener actionListener) {
    setLayout(null);
    setBounds(5, 30, 1020, 510);
  }

  @Override
  public void paint(Graphics g) {
    Graphics2D graphics2D = (Graphics2D) g;
    AffineTransform backupTransform = (AffineTransform) graphics2D.getTransform().clone();
    Rectangle backupClipBounds = (Rectangle) graphics2D.getClipBounds().clone();

    graphics2D.translate(5, 5);
    graphics2D.clipRect(0, 0, 500, 500);
    drawLeftCanvas(graphics2D);
    graphics2D.setTransform((AffineTransform) backupTransform.clone());
    graphics2D.setClip(
        backupClipBounds.x, backupClipBounds.y, backupClipBounds.width, backupClipBounds.height);

    graphics2D.translate(515, 5);
    graphics2D.clipRect(0, 0, 500, 500);
    drawRightCanvas(graphics2D);
    graphics2D.setTransform(backupTransform);
    graphics2D.setClip(
        backupClipBounds.x, backupClipBounds.y, backupClipBounds.width, backupClipBounds.height);

    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setColor(Color.decode("#171F27"));
    graphics2D.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    graphics2D.drawRect(5, 5, 500, 500);
    graphics2D.drawRect(515, 5, 500, 500);

    paintComponents(graphics2D);
  }

  protected abstract void drawLeftCanvas(Graphics2D graphics2D);

  protected abstract void drawRightCanvas(Graphics2D graphics2D);
}
