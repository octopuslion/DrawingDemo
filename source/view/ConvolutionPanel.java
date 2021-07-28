package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import resource.Resource;

public class ConvolutionPanel extends MainPanel {

  private static final long serialVersionUID = 4143230983069658089L;
  private final JRadioButton kernel1RadioButton;
  private final JRadioButton kernel2RadioButton;
  private final JRadioButton kernel3RadioButton;
  private final JRadioButton kernel4RadioButton;
  private final ButtonGroup buttonGroup;
  private final JLabel varianceLabel;
  private final JComboBox<String> varianceComboBox;
  private final JLabel radiusLabel;
  private final JComboBox<String> radiusComboBox;
  private final JButton updateButton;
  private BufferedImage leftImage;
  private BufferedImage rightImage;
  private double[][] kernel;

  public ConvolutionPanel(Font componentFont) {
    super(componentFont);
    kernel1RadioButton = new JRadioButton();
    kernel2RadioButton = new JRadioButton();
    kernel3RadioButton = new JRadioButton();
    kernel4RadioButton = new JRadioButton();
    buttonGroup = new ButtonGroup();
    varianceLabel = new JLabel();
    varianceComboBox = new JComboBox<>();
    radiusLabel = new JLabel();
    radiusComboBox = new JComboBox<>();
    updateButton = new JButton();
    leftImage = null;
    rightImage = null;
    kernel = null;
  }

  @Override
  public void initialize(ActionListener actionListener) {
    super.initialize(actionListener);
    leftImage = Resource.getPNG_SAMPLE_01();
    Font componentFont = getComponentFont();

    kernel1RadioButton.setName("ConvolutionPanel:KernelRadioButton");
    kernel1RadioButton.setFont(componentFont);
    kernel1RadioButton.setText("平均");
    kernel1RadioButton.setBounds(5, 5, 60, 20);
    kernel1RadioButton.setOpaque(true);
    kernel1RadioButton.addActionListener(actionListener);
    buttonGroup.add(kernel1RadioButton);
    add(kernel1RadioButton);

    kernel2RadioButton.setName("ConvolutionPanel:KernelRadioButton");
    kernel2RadioButton.setFont(componentFont);
    kernel2RadioButton.setText("高斯");
    kernel2RadioButton.setBounds(70, 5, 60, 20);
    kernel2RadioButton.setOpaque(true);
    kernel2RadioButton.addActionListener(actionListener);
    buttonGroup.add(kernel2RadioButton);
    add(kernel2RadioButton);

    kernel3RadioButton.setName("ConvolutionPanel:KernelRadioButton");
    kernel3RadioButton.setFont(componentFont);
    kernel3RadioButton.setText("锐化");
    kernel3RadioButton.setBounds(135, 5, 60, 20);
    kernel3RadioButton.setOpaque(true);
    kernel3RadioButton.addActionListener(actionListener);
    buttonGroup.add(kernel3RadioButton);
    add(kernel3RadioButton);

    kernel4RadioButton.setName("ConvolutionPanel:KernelRadioButton");
    kernel4RadioButton.setFont(componentFont);
    kernel4RadioButton.setText("自定义高斯");
    kernel4RadioButton.setBounds(200, 5, 110, 20);
    kernel4RadioButton.setOpaque(true);
    kernel4RadioButton.addActionListener(actionListener);
    buttonGroup.add(kernel4RadioButton);
    add(kernel4RadioButton);

    varianceLabel.setBounds(315, 5, 50, 20);
    varianceLabel.setFont(componentFont);
    varianceLabel.setText("方差：");
    add(varianceLabel);

    varianceComboBox.setBounds(350, 5, 70, 20);
    varianceComboBox.setFont(componentFont);
    varianceComboBox.addItem("1");
    varianceComboBox.addItem("2");
    varianceComboBox.addItem("3");
    varianceComboBox.setSelectedIndex(0);
    add(varianceComboBox);

    radiusLabel.setBounds(425, 5, 50, 20);
    radiusLabel.setFont(componentFont);
    radiusLabel.setText("半径：");
    add(radiusLabel);

    radiusComboBox.setBounds(460, 5, 70, 20);
    radiusComboBox.setFont(componentFont);
    radiusComboBox.addItem("1");
    radiusComboBox.addItem("3");
    radiusComboBox.addItem("5");
    radiusComboBox.setSelectedIndex(0);
    add(radiusComboBox);

    updateButton.setName("ConvolutionPanel:UpdateButton");
    updateButton.setBounds(535, 5, 40, 20);
    updateButton.setFont(componentFont);
    updateButton.setText("更新");
    updateButton.addActionListener(actionListener);
    add(updateButton);
  }

  public void switchKernel() {
    if (kernel1RadioButton.isSelected()) {
      // 平滑，均值滤波卷积核，3*3。
      kernel =
          new double[][] {
            {1.0 / 9, 1.0 / 9, 1.0 / 9}, {1.0 / 9, 1.0 / 9, 1.0 / 9}, {1.0 / 9, 1.0 / 9, 1.0 / 9}
          };
    } else if (kernel2RadioButton.isSelected()) {
      // 平滑，高斯滤波卷积核，5*5。
      kernel =
          new double[][] {
            {1.0 / 256, 4.0 / 256, 6.0 / 256, 4.0 / 256, 1.0 / 256},
            {4.0 / 256, 16.0 / 256, 24.0 / 256, 16.0 / 256, 4.0 / 256},
            {6.0 / 256, 24.0 / 256, 36.0 / 256, 24.0 / 256, 6.0 / 256},
            {4.0 / 256, 16.0 / 256, 24.0 / 256, 16.0 / 256, 4.0 / 256},
            {1.0 / 256, 4.0 / 256, 6.0 / 256, 4.0 / 256, 1.0 / 256}
          };
    } else if (kernel3RadioButton.isSelected()) {
      // 锐化，3*3。
      kernel = new double[][] {{-1, -1, -1}, {-1, 9, -1}, {-1, -1, -1}};
    } else {
      // 自定义高斯卷积核。
      double variance = getValue(varianceComboBox);
      int radius = getValue(radiusComboBox);
      kernel = getCustomKernel(variance, radius);
    }

    rightImage = convolute(leftImage, kernel);
    updateUI();
  }

  public void updateKernel() {
    if (kernel4RadioButton.isSelected()) {
      // 更新自定义高斯卷积核。
      double variance = getValue(varianceComboBox);
      int radius = getValue(radiusComboBox);
      kernel = getCustomKernel(variance, radius);
      rightImage = convolute(leftImage, kernel);
      updateUI();
    }
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

  private int getValue(JComboBox<String> comboBox) {
    String text = (String) comboBox.getSelectedItem();
    if (text != null) {
      return Integer.parseInt(text);
    } else {
      return 1;
    }
  }

  private double[][] getCustomKernel(double variance, int radius) {
    double[][] kernel = new double[2 * radius + 1][2 * radius + 1];

    double sum = 0;
    for (int j = 0; j < kernel.length; j++) {
      for (int i = 0; i < kernel[0].length; i++) {
        int x = i - radius;
        int y = j - radius;

        // 使用二维高斯公式计算得到相应位置的高斯值。
        kernel[j][i] =
            Math.pow(Math.E, -(x * x + y * y) / (2 * variance * variance))
                / (2 * Math.PI * variance * variance);
        sum += kernel[j][i];
      }
    }

    // 进行归一化。
    for (int j = 0; j < kernel.length; j++) {
      for (int i = 0; i < kernel[0].length; i++) {
        kernel[j][i] /= sum;
      }
    }

    return kernel;
  }

  private BufferedImage convolute(BufferedImage sourceImage, double[][] kernel) {
    Color[][] sourceColors = getImageColors(sourceImage, kernel.length);
    Color[][] targetColors = new Color[sourceColors.length][sourceColors[0].length];

    // 进行卷积操作，对RGB三个颜色通道分别进行卷积运算，透明度不参与卷积。
    for (int j = kernel.length; j < sourceColors.length - kernel.length; j++) {
      for (int i = kernel.length; i < sourceColors[0].length - kernel.length; i++) {
        double red = 0;
        double green = 0;
        double blue = 0;
        for (int n = 0; n < kernel.length; n++) {
          for (int m = 0; m < kernel.length; m++) {
            Color sourceColor = sourceColors[j - kernel.length / 2 + n][i - kernel.length / 2 + m];
            red += sourceColor.getRed() * kernel[n][m];
            green += sourceColor.getGreen() * kernel[n][m];
            blue += sourceColor.getBlue() * kernel[n][m];
          }
        }

        targetColors[j][i] =
            new Color(
                toColorComponent(red),
                toColorComponent(green),
                toColorComponent(blue),
                sourceColors[j][i].getAlpha());
      }
    }

    // 通过卷积运算后的像素点颜色构造新的图像。
    int imageWidth = sourceImage.getWidth();
    int imageHeight = sourceImage.getHeight();
    BufferedImage targetImage =
        new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
    for (int j = 0; j < imageHeight; j++) {
      for (int i = 0; i < imageWidth; i++) {
        targetImage.setRGB(i, j, targetColors[j + kernel.length][i + kernel.length].getRGB());
      }
    }

    return targetImage;
  }

  private Color[][] getImageColors(BufferedImage image, int kernelSize) {
    // 获取图像的所有像素点颜色，这里采用补零填充法。
    int width = image.getWidth();
    int height = image.getHeight();
    int virtualWidth = width + kernelSize * 2;
    int virtualHeight = height + kernelSize * 2;
    Color[][] colors = new Color[virtualHeight][virtualWidth];
    for (int j = 0; j < virtualHeight; j++) {
      for (int i = 0; i < virtualWidth; i++) {
        if (i >= kernelSize
            && i < width + kernelSize
            && j >= kernelSize
            && j < height + kernelSize) {
          int colorValue = image.getRGB(i - kernelSize, j - kernelSize);
          int alpha = colorValue >>> 24;
          int red = (colorValue >> 16) & 0xff;
          int green = (colorValue >> 8) & 0xff;
          int blue = colorValue & 0xff;
          colors[j][i] = new Color(red, green, blue, alpha);
        } else {
          colors[j][i] = new Color(0, 0, 0);
        }
      }
    }

    return colors;
  }

  private int toColorComponent(double value) {
    int validValue = (int) value;
    if (validValue < 0) {
      validValue = 0;
    } else if (validValue > 255) {
      validValue = 255;
    }

    return validValue;
  }
}
