package controller;

import view.MainFrame;

public class MainFrameController {

  private MainFrame mainFrame;

  public MainFrameController() {}

  public void initialize() {
    mainFrame = new MainFrame();
  }

  public void show() {
    mainFrame.setVisible(true);
  }
}
