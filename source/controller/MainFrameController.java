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
import view.CompressionPanel;
import view.ConvolutionPanel;
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
  private final ConvolutionPanel convolutionPanel;
  private final CompressionPanel compressionPanel;
  private JPanel mainFramePanel;

  public MainFrameController() {
    Font componentFont = new Font("微软雅黑", Font.PLAIN, 14);
    mainFrame = new MainFrame(componentFont);
    gradientPanel = new GradientPanel(componentFont);
    shadowPanel = new ShadowPanel(componentFont);
    glowPanel = new GlowPanel(componentFont);
    animationPanel = new AnimationPanel(componentFont);
    convolutionPanel = new ConvolutionPanel(componentFont);
    compressionPanel = new CompressionPanel(componentFont);
    mainFramePanel = null;
  }

  public void initialize() {
    mainFrame.initialize(this, this);
    gradientPanel.initialize(this);
    shadowPanel.initialize(this);
    glowPanel.initialize(this);
    animationPanel.initialize(this);
    convolutionPanel.initialize(this);
    compressionPanel.initialize(this);
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
    // 控件操作事件处理。
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
    } else if ("MainFrame:ConvolutionShowButton".equals(name)) {
      updateMainFramePanel(convolutionPanel);
    } else if ("MainFrame:CompressionShowButton".equals(name)) {
      updateMainFramePanel(compressionPanel);
    } else if ("MainFrame:RefreshButton".equals(name)) {
      mainFrame.setSize(1030, 590);
      if (mainFramePanel != null) {
        mainFramePanel.updateUI();
      }
    } else if ("AnimationPanel:ToggleButton".equals(name)) {
      animationPanel.togglePlayType();
    } else if ("ConvolutionPanel:KernelRadioButton".equals(name)) {
      convolutionPanel.switchKernel();
    } else if ("CompressionPanel:AlgorithmRadioButton".equals(name)) {
      compressionPanel.switchAlgorithm();
    }
  }

  private void updateMainFramePanel(JPanel panel) {
    // 更新展示面板。
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
