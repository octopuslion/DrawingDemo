package view;

import Interpolator.ImageBSplineInterpolator;
import Interpolator.ImageBilinearInterpolator;
import Interpolator.ImageClosestNeighborInterpolator;
import Interpolator.ImageCubicConvolutionInterpolator;
import Interpolator.ImageInterpolator;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import resource.Resource;

public class CompressionPanel extends MainPanel {

  private static final long serialVersionUID = -2086515989812393618L;
  private final JLabel imageSizeLabel;
  private final JComboBox<String> imageSizeComboBox;
  private final JRadioButton algorithm1RadioButton;
  private final JRadioButton algorithm2RadioButton;
  private final JRadioButton algorithm3RadioButton;
  private final JRadioButton algorithm4RadioButton;
  private final ButtonGroup buttonGroup;
  private BufferedImage leftImage;
  private BufferedImage rightImage;
  private ImageInterpolator imageInterpolator;

  public CompressionPanel(Font componentFont) {
    super(componentFont);
    imageSizeLabel = new JLabel();
    imageSizeComboBox = new JComboBox<>();
    algorithm1RadioButton = new JRadioButton();
    algorithm2RadioButton = new JRadioButton();
    algorithm3RadioButton = new JRadioButton();
    algorithm4RadioButton = new JRadioButton();
    buttonGroup = new ButtonGroup();
    leftImage = null;
    rightImage = null;
    imageInterpolator = null;
  }

  @Override
  public void initialize(ActionListener actionListener) {
    super.initialize(actionListener);
    leftImage = Resource.getPNG_SAMPLE_01();
    Font componentFont = getComponentFont();

    imageSizeLabel.setBounds(5, 5, 100, 20);
    imageSizeLabel.setFont(componentFont);
    imageSizeLabel.setText("图片尺寸：");
    add(imageSizeLabel);

    imageSizeComboBox.setBounds(70, 5, 100, 20);
    imageSizeComboBox.setFont(componentFont);
    imageSizeComboBox.addItem("30%");
    imageSizeComboBox.addItem("70%");
    imageSizeComboBox.addItem("150%");
    imageSizeComboBox.addItem("300%");
    imageSizeComboBox.setSelectedIndex(0);
    add(imageSizeComboBox);

    algorithm1RadioButton.setName("CompressionPanel:AlgorithmRadioButton");
    algorithm1RadioButton.setFont(componentFont);
    algorithm1RadioButton.setText("最近邻");
    algorithm1RadioButton.setBounds(175, 5, 80, 20);
    algorithm1RadioButton.setOpaque(true);
    algorithm1RadioButton.addActionListener(actionListener);
    buttonGroup.add(algorithm1RadioButton);
    add(algorithm1RadioButton);

    algorithm2RadioButton.setName("CompressionPanel:AlgorithmRadioButton");
    algorithm2RadioButton.setFont(componentFont);
    algorithm2RadioButton.setText("线性");
    algorithm2RadioButton.setBounds(260, 5, 60, 20);
    algorithm2RadioButton.setOpaque(true);
    algorithm2RadioButton.addActionListener(actionListener);
    buttonGroup.add(algorithm2RadioButton);
    add(algorithm2RadioButton);

    algorithm3RadioButton.setName("CompressionPanel:AlgorithmRadioButton");
    algorithm3RadioButton.setFont(componentFont);
    algorithm3RadioButton.setText("B样条");
    algorithm3RadioButton.setBounds(325, 5, 80, 20);
    algorithm3RadioButton.setOpaque(true);
    algorithm3RadioButton.addActionListener(actionListener);
    buttonGroup.add(algorithm3RadioButton);
    add(algorithm3RadioButton);

    algorithm4RadioButton.setName("CompressionPanel:AlgorithmRadioButton");
    algorithm4RadioButton.setFont(componentFont);
    algorithm4RadioButton.setText("三次卷积");
    algorithm4RadioButton.setBounds(410, 5, 100, 20);
    algorithm4RadioButton.setOpaque(true);
    algorithm4RadioButton.addActionListener(actionListener);
    buttonGroup.add(algorithm4RadioButton);
    add(algorithm4RadioButton);
  }

  public void switchAlgorithm() {
    int imageSize = getImageSize();
    if (algorithm1RadioButton.isSelected()) {
      // 最近邻算法。
      imageInterpolator = new ImageClosestNeighborInterpolator(imageSize, imageSize);
    } else if (algorithm2RadioButton.isSelected()) {
      // 双线性插值算法。
      imageInterpolator = new ImageBilinearInterpolator(imageSize, imageSize);
    } else if (algorithm3RadioButton.isSelected()) {
      // B样条采样算法。
      imageInterpolator = new ImageBSplineInterpolator(imageSize, imageSize);
    } else {
      // 三次卷积采样算法。
      imageInterpolator = new ImageCubicConvolutionInterpolator(imageSize, imageSize);
    }

    rightImage = imageInterpolator.compress(leftImage);
    updateUI();
  }

  @Override
  protected void drawLeftCanvas(Graphics2D graphics2D) {
    graphics2D.drawImage(leftImage, 5, 5, leftImage.getWidth(), leftImage.getHeight(), null);
  }

  @Override
  protected void drawRightCanvas(Graphics2D graphics2D) {
    BufferedImage drawingImage = rightImage == null ? leftImage : rightImage;
    graphics2D.drawImage(
        drawingImage, 5, 5, drawingImage.getWidth(), drawingImage.getHeight(), null);
  }

  private int getImageSize() {
    int size = 490;
    String text = (String) imageSizeComboBox.getSelectedItem();
    if (text != null && text.endsWith("%")) {
      text = text.substring(0, text.length() - 1);
      try {
        size = (int) (490 * 0.01 * Integer.parseInt(text));
      } catch (Exception ignored) {
      }
    }

    return size;
  }
}
