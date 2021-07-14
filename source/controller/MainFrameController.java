package controller;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;
import view.AnimationPanel;
import view.GlowPanel;
import view.GradientPanel;
import view.MainFrame;
import view.ShadowPanel;

public class MainFrameController extends WindowAdapter implements ActionListener {

  private final MainFrame mainFrame;
  private final GradientPanel gradientPanel;
  private final ShadowPanel shadowPanel;
  private final GlowPanel glowPanel;
  private final AnimationPanel animationPanel;
  private JPanel mainFramePanel;

  public MainFrameController() {
    Font componentFont = new Font("微软雅黑", Font.PLAIN, 14);
    mainFrame = new MainFrame(componentFont);
    mainFramePanel = null;
    gradientPanel = new GradientPanel(componentFont);
    shadowPanel = new ShadowPanel(componentFont);
    glowPanel = new GlowPanel(componentFont);
    animationPanel = new AnimationPanel(componentFont);
  }

  public void initialize() {
    mainFrame.initialize(this, this);
    gradientPanel.initialize(this);
    shadowPanel.initialize(this);
    glowPanel.initialize(this);
    animationPanel.initialize(this);
  }

  public void show() {
    mainFrame.setVisible(true);
  }

  @Override
  public void windowClosing(WindowEvent e) {
    super.windowClosing(e);
    animationPanel.animationStop();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Component component = (Component) e.getSource();
    if (component == null) {
      return;
    }

    String name = component.getName();
    if ("MainFrame:GradientShowButton".equals(name)) {
      updateMainFramePanel(gradientPanel);
    } else if ("MainFrame:ShadowShowButton".equals(name)) {
      updateMainFramePanel(shadowPanel);
    } else if ("MainFrame:GlowShowButton".equals(name)) {
      updateMainFramePanel(glowPanel);
    } else if ("MainFrame:AnimationShowButton".equals(name)) {
      updateMainFramePanel(animationPanel);
      animationPanel.animationStart();
    } else if ("MainFrame:RefreshButton".equals(name)) {
      mainFrame.setSize(1030, 590);
      if (mainFramePanel != null) {
        mainFramePanel.updateUI();
      }
    } else if ("AnimationPanel:ToggleButton".equals(name)) {
      animationPanel.statusToggle();
    }
  }

  private void updateMainFramePanel(JPanel panel) {
    Container container = mainFrame.getContentPane();
    if (mainFramePanel != null) {
      container.remove(mainFramePanel);
      if (mainFramePanel instanceof AnimationPanel) {
        ((AnimationPanel) mainFramePanel).animationStop();
      }
    }

    mainFramePanel = panel;
    container.add(mainFramePanel);
    mainFramePanel.updateUI();
    if (mainFramePanel instanceof AnimationPanel) {
      ((AnimationPanel) mainFramePanel).animationStart();
    }
  }
}
