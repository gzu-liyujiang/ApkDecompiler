package cn.qqtheme.ApkDec;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Created by liyujiang on 16-8-12.
 */
public class MainWindow {
    private String toolPath = Constants.TOOL_PATH;
    private JPanel panelRoot;
    private JTextField inputApk;
    private JButton buttonDecodeXml;
    private JButton buttonDecodeJar;
    private JButton buttonJdGui;
    private JTextArea logArea;
    private JButton buttonOpenFile;
    private String apkPath;

    private MainWindow() {
        if (!toolPath.endsWith("/")) {
            toolPath += "/";
        }
        logArea.append("\n\n");
        //This inspection reports all anonymous classes which can be replaced with lambda expressions
        //Lambda syntax is not supported under Java 1.7 or earlier JVMs.
        buttonOpenFile.addActionListener(e -> chooseApk());
        buttonDecodeXml.addActionListener(e -> {
            if (apkValid()) {

            }
        });
        buttonDecodeJar.addActionListener(e -> {
            if (apkValid()) {

            }
        });
        buttonJdGui.addActionListener(e -> {
            String command = toolPath + "tools/jd-gui";
            logArea.append("exec command: " + command + "\n");
            try {
                Runtime.getRuntime().exec(command);
            } catch (IOException e1) {
                logArea.append("error: " + e1 + "\n");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(Constants.APP_NAME + Constants.APP_VERSION);
        MainWindow mainWindow = new MainWindow();
        URL iconUrl = MainWindow.class.getResource("/icons/liyujiang.png");
        mainWindow.logArea.append("icon path: " + iconUrl + "\n");
        if (iconUrl != null) {
            frame.setIconImage(new ImageIcon(iconUrl).getImage());
        }
        frame.setContentPane(mainWindow.panelRoot);
        frame.setJMenuBar(mainWindow.createMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(new Dimension(480, 320));
        frame.setResizable(false);//不允许缩放
        frame.setLocationRelativeTo(null);//居中
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuMain = new JMenu("菜单");
        JMenuItem menuHelp = new JMenuItem("帮助");
        menuHelp.addActionListener(e -> help());
        JMenuItem menuAbout = new JMenuItem("关于");
        menuAbout.addActionListener(e -> about());
        menuMain.add(menuHelp);
        menuMain.add(menuAbout);
        menuBar.add(menuMain);
        return menuBar;
    }

    private void help() {
        JOptionPane.showMessageDialog(panelRoot,
                "1、点击浏览选择一个需要反编译的apk文件；\n" +
                        "2、点击反编译xml以及dex2jar；\n" +
                        "3、点击打开jd-gui，查看反编译后的jar的源代码。\n",
                "帮助", JOptionPane.INFORMATION_MESSAGE);
    }

    private void about() {
        JOptionPane.showMessageDialog(panelRoot,
                "开发工具：Intellij IDEA 2016.2.1\n\n" +
                        "制作：穿青人@李玉江[QQ:1032694760]",
                "关于", JOptionPane.INFORMATION_MESSAGE);
    }

    private void chooseApk() {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new ApkFilter());
        int flag = chooser.showOpenDialog(panelRoot);
        if (flag == JFileChooser.APPROVE_OPTION) {
            apkPath = chooser.getSelectedFile().getAbsolutePath();
            inputApk.setText(apkPath.replaceAll("\\\\", "/"));
        }
    }

    private boolean apkValid() {
        if (apkPath == null || apkPath.trim().length() == 0) {
            JOptionPane.showMessageDialog(panelRoot, "请先选择要反编译的apk文件");
            return false;
        }
        return true;
    }

}
