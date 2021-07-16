package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import resource.Resource;

public class ConvolutionPanel extends MainPanel {

  private static final long serialVersionUID = 4143230983069658089L;
  private final JRadioButton kernel1RadioButton;
  private final JRadioButton kernel2RadioButton;
  private final JRadioButton kernel3RadioButton;
  private final ButtonGroup buttonGroup;
  private BufferedImage leftImage;
  private BufferedImage rightImage;
  private double[][] kernel;

  public ConvolutionPanel(Font componentFont) {
    super(componentFont);
    kernel1RadioButton = new JRadioButton();
    kernel2RadioButton = new JRadioButton();
    kernel3RadioButton = new JRadioButton();
    buttonGroup = new ButtonGroup();
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
    } else {
      // 锐化，3*3。
      kernel = new double[][] {{-1, -1, -1}, {-1, 9, -1}, {-1, -1, -1}};
    }

    rightImage = convolute(leftImage, kernel);
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

  private BufferedImage convolute(BufferedImage sourceImage, double[][] kernel) {
    Color[][] sourceColors = getImageColors(sourceImage, kernel.length);
    Color[][] targetColors = new Color[sourceColors[0].length][sourceColors.length];

    // 进行卷积操作，对RGB三个颜色通道分别进行卷积运算，透明度不参与卷积。
    for (int i = kernel.length; i < sourceColors[0].length - kernel.length; i++) {
      for (int j = kernel.length; j < sourceColors.length - kernel.length; j++) {
        double red = 0;
        double green = 0;
        double blue = 0;
        for (int m = 0; m < kernel.length; m++) {
          for (int n = 0; n < kernel.length; n++) {
            Color sourceColor = sourceColors[i - kernel.length / 2 + m][j - kernel.length / 2 + n];
            red += sourceColor.getRed() * kernel[m][n];
            green += sourceColor.getGreen() * kernel[m][n];
            blue += sourceColor.getBlue() * kernel[m][n];
          }
        }

        targetColors[i][j] =
            new Color(
                toColorComponent(red),
                toColorComponent(green),
                toColorComponent(blue),
                sourceColors[i][j].getAlpha());
      }
    }

    // 通过卷积运算后的像素点颜色构造新的图像。
    int imageWidth = sourceImage.getWidth();
    int imageHeight = sourceImage.getHeight();
    BufferedImage targetImage =
        new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
    for (int i = 0; i < imageWidth; i++) {
      for (int j = 0; j < imageHeight; j++) {
        targetImage.setRGB(i, j, targetColors[i + kernel.length][j + kernel.length].getRGB());
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
    Color[][] colors = new Color[virtualWidth][virtualHeight];
    for (int i = 0; i < virtualWidth; i++) {
      for (int j = 0; j < virtualHeight; j++) {
        if (i >= kernelSize
            && i < width + kernelSize
            && j >= kernelSize
            && j < height + kernelSize) {
          int colorValue = image.getRGB(i - kernelSize, j - kernelSize);
          int alpha = colorValue >>> 24;
          int red = (colorValue >> 16) & 0xff;
          int green = (colorValue >> 8) & 0xff;
          int blue = colorValue & 0xff;
          colors[i][j] = new Color(red, green, blue, alpha);
        } else {
          colors[i][j] = new Color(0, 0, 0);
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
