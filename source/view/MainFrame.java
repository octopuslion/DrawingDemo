package view;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class MainFrame extends JFrame {

  private static final long serialVersionUID = 8456560429229699542L;

  private final Font componentFont;
  private final JButton gradientShowButton;
  private final JButton shadowShowButton;
  private final JButton glowShowButton;
  private final JButton animationShowButton;
  private final JButton refreshButton;

  public MainFrame(Font componentFont) {
    this.componentFont = componentFont;
    gradientShowButton = new JButton();
    shadowShowButton = new JButton();
    glowShowButton = new JButton();
    animationShowButton = new JButton();
    refreshButton = new JButton();
  }

  public void initialize(WindowListener windowListener, ActionListener actionListener) {
    setLayout(null);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 1030, 590);
    addWindowListener(windowListener);
    Container container = getContentPane();

    gradientShowButton.setName("MainFrame:GradientShowButton");
    gradientShowButton.setBounds(5, 5, 40, 20);
    gradientShowButton.setFont(componentFont);
    gradientShowButton.setText("渐变");
    gradientShowButton.addActionListener(actionListener);
    container.add(gradientShowButton);

    shadowShowButton.setName("MainFrame:ShadowShowButton");
    shadowShowButton.setBounds(50, 5, 40, 20);
    shadowShowButton.setFont(componentFont);
    shadowShowButton.setText("阴影");
    shadowShowButton.addActionListener(actionListener);
    container.add(shadowShowButton);

    glowShowButton.setName("MainFrame:GlowShowButton");
    glowShowButton.setBounds(95, 5, 40, 20);
    glowShowButton.setFont(componentFont);
    glowShowButton.setText("发光");
    glowShowButton.addActionListener(actionListener);
    container.add(glowShowButton);

    animationShowButton.setName("MainFrame:AnimationShowButton");
    animationShowButton.setBounds(140, 5, 40, 20);
    animationShowButton.setFont(componentFont);
    animationShowButton.setText("动画");
    animationShowButton.addActionListener(actionListener);
    container.add(animationShowButton);

    refreshButton.setName("MainFrame:RefreshButton");
    refreshButton.setBounds(980, 545, 40, 20);
    refreshButton.setFont(componentFont);
    refreshButton.setText("刷新");
    refreshButton.addActionListener(actionListener);
    add(refreshButton);
  }
}
