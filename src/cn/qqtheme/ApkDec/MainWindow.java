/*
 * Copyright (c) 2019. Li Yujiang, Chuanqing People. All rights reserved.
 */

package cn.qqtheme.ApkDec;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URLDecoder;

/**
 * Java桌面软件开发本人是菜鸟，Swing不太熟
 * Created by liyujiang on 2016-8-12.
 *
 * @author 李玉江[1032694760@qq.com]
 */
@SuppressWarnings("WeakerAccess")
public class MainWindow {
    private static final MainWindow MAIN_WINDOW = new MainWindow();
    private String appPath;
    private String toolPath;
    private JPanel panelRoot;
    private JTextField inputApk;
    private JButton buttonDecodeXml;
    private JButton buttonDecodeJar;
    private JTextArea logArea;
    private JButton buttonOpenFile;
    private JButton logClear;
    private JButton buttonOpenDir;
    private String filePath;
    private String dirPath;
    private String jarPath;

    private MainWindow() {
        showLog("\n\n");
        //获取当前执行的jar包的绝对路径
        String path = MainWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        appPath = new File(path).getParentFile().getAbsolutePath();
        appPath = appPath.replaceAll("\\\\", "/");
        try {
            appPath = URLDecoder.decode(appPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!appPath.endsWith("/")) {
            appPath += "/";
        }
        toolPath = appPath + "tools/";
        dirPath = null;
        jarPath = null;
        showLog("程序包路径为：" + appPath);
        addActionListeners();
    }

    public static MainWindow getInstance() {
        return MAIN_WINDOW;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(Config.APP_NAME + " " + Config.APP_VERSION);
        frame.setIconImage(new ImageIcon(MAIN_WINDOW.appPath + "icons/default.png").getImage());
        frame.setContentPane(MAIN_WINDOW.panelRoot);
        frame.setJMenuBar(MAIN_WINDOW.createMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(Config.WINDOW_RESIZABLE);
        frame.setMinimumSize(new Dimension(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public final JTextArea getLogArea() {
        return logArea;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(255, 255, 255));
        JButton menuJdGui = new JButton("JD-GUI");
        menuJdGui.setBackground(new Color(255, 255, 255));
        menuJdGui.setMargin(new Insets(0, 25, 0, 25));
        menuJdGui.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openJdGui();
            }
        });
        JButton menuAbout = new JButton("关于");
        menuAbout.setBackground(new Color(255, 255, 255));
        menuAbout.setMargin(new Insets(0, 25, 0, 25));
        menuAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.about();
            }
        });
        menuBar.add(menuJdGui);
        menuBar.add(menuAbout);
        return menuBar;
    }

    private void addActionListeners() {
        //This inspection reports all anonymous classes which can be replaced with lambda expressions
        //Lambda syntax is not supported under Java 1.7 or earlier JVMs.
        buttonOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile(new String[]{"apk", "jar"});
            }
        });
        buttonDecodeXml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileValid("apk", true)) {
                    dirPath = filePath.replace(".apk", "");
                    String cmd = toolPath + "apktool/apktool --advanced decode --force --output " + dirPath + " --no-src --frame-path " + toolPath + "apktool/ " + filePath;
                    new CmdThread(cmd).start();
                }
            }
        });
        buttonDecodeJar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileValid("apk", false)) {
                    jarPath = filePath.replace(".apk", ".jar");
                    String cmd = toolPath + "dex2jar/d2j-dex2jar.sh" + " --force --output " + jarPath + " --print-ir " + filePath;
                    new CmdThread(cmd).start();
                } else if (fileValid("jar", false)) {
                    openJdGui(filePath);
                } else {
                    JOptionPane.showMessageDialog(panelRoot, "请先选择apk或jar文件");
                }
            }
        });
        buttonOpenDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDir();
            }
        });
        logClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("##### 欢迎使用♪(^∇^*)。\n\n");
            }
        });
    }

    private void about() {
        JOptionPane.showMessageDialog(panelRoot, Config.APP_ABOUT,
                "关于", JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(appPath + "icons/liyujiang.png")
        );
    }

    private void chooseFile(String[] allowExt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new ExtensionFilter(allowExt));
        int flag = chooser.showOpenDialog(panelRoot);
        if (flag == JFileChooser.APPROVE_OPTION) {
            filePath = chooser.getSelectedFile().getAbsolutePath();
            filePath = filePath.replaceAll("\\\\", "/");
            inputApk.setText(filePath);
        }
    }

    public void openDir() {
        if (filePath == null || filePath.trim().length() == 0) {
            JOptionPane.showMessageDialog(panelRoot, "请先选择文件");
            return;
        }
        File file = new File(dirPath != null ? dirPath : filePath);
        try {
            Desktop.getDesktop().open(file.getParentFile());
        } catch (IOException e) {
            showLog("出错了：" + e);
        }
    }

    public void showLog(String log) {
        logArea.append(log + "\n");
        //logArea.setCaretPosition(logArea.getDocument().getLength());
        logArea.setSelectionStart(logArea.getText().length());
    }

    private boolean fileValid(String ext, boolean showTips) {
        if (filePath == null || !filePath.trim().toLowerCase().endsWith('.' + ext)) {
            String msg = "请先选择." + ext + "文件";
            if (showTips) {
                JOptionPane.showMessageDialog(panelRoot, msg);
            }
            return false;
        }
        return true;
    }

    public void openJdGui() {
        openJdGui(null);
    }

    public void openJdGui(String path) {
        if (path == null || path.trim().length() == 0) {
            path = jarPath;
        }
        String cmd = "java -jar " + toolPath + "jd-gui.jar";
        if (path != null && path.trim().length() > 0) {
            cmd += " " + path;
        }
        new CmdThread(cmd).start();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelRoot = new JPanel();
        panelRoot.setLayout(new FormLayout("left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panelRoot.setBackground(new Color(-1180673));
        panelRoot.setMaximumSize(new Dimension(1090, 1208));
        panelRoot.setMinimumSize(new Dimension(320, 240));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBackground(new Color(-1115649));
        CellConstraints cc = new CellConstraints();
        panelRoot.add(scrollPane1, cc.xyw(2, 7, 5, CellConstraints.FILL, CellConstraints.FILL));
        logArea = new JTextArea();
        logArea.setBackground(new Color(-16777216));
        logArea.setColumns(0);
        logArea.setEditable(false);
        logArea.setForeground(new Color(-16404459));
        logArea.setLineWrap(true);
        logArea.setMargin(new Insets(0, 0, 0, 0));
        logArea.setRows(10);
        logArea.setSelectionEnd(12);
        logArea.setSelectionStart(12);
        logArea.setText("欢迎使用♪(^∇^*)。");
        logArea.setToolTipText("");
        scrollPane1.setViewportView(logArea);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:d:grow", "center:d:grow"));
        panel1.setBackground(new Color(-1050369));
        panelRoot.add(panel1, cc.xyw(2, 3, 5, CellConstraints.CENTER, CellConstraints.DEFAULT));
        buttonDecodeXml = new JButton();
        buttonDecodeXml.setBackground(new Color(-1180673));
        buttonDecodeXml.setMargin(new Insets(5, 21, 5, 21));
        buttonDecodeXml.setText("反编并查看xml文件");
        buttonDecodeXml.setToolTipText("从apk文件中把二进制的xml、arsc等资源文件转换为文本格式");
        panel1.add(buttonDecodeXml, cc.xy(1, 1));
        buttonDecodeJar = new JButton();
        buttonDecodeJar.setBackground(new Color(-1180673));
        buttonDecodeJar.setMargin(new Insets(5, 21, 5, 21));
        buttonDecodeJar.setText("反编并查看Java源码");
        buttonDecodeJar.setToolTipText("从apk文件中把dex转换为jar并查看Java源码");
        panel1.add(buttonDecodeJar, cc.xy(3, 1));
        buttonOpenDir = new JButton();
        buttonOpenDir.setBackground(new Color(-1180161));
        buttonOpenDir.setText("打开文件所在目录");
        buttonOpenDir.setToolTipText("打开刚刚选择的apk文件所在目录");
        panel1.add(buttonOpenDir, cc.xy(5, 1));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setBackground(new Color(-1542));
        panelRoot.add(panel2, new CellConstraints(2, 5, 5, 1, CellConstraints.DEFAULT, CellConstraints.FILL, new Insets(8, 0, 8, 0)));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 20), null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(20, 0, 20, 0), -1, -1));
        panel3.setBackground(new Color(-1180673));
        panelRoot.add(panel3, new CellConstraints(2, 1, 5, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(0, 0, 0, 8)));
        inputApk = new JTextField();
        inputApk.setAutoscrolls(false);
        inputApk.setEditable(false);
        inputApk.setEnabled(true);
        inputApk.setForeground(new Color(-8618884));
        inputApk.setMargin(new Insets(3, 9, 3, 9));
        inputApk.setText("");
        inputApk.setToolTipText("待反编译的apk路径");
        panel3.add(inputApk, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonOpenFile = new JButton();
        buttonOpenFile.setAlignmentX(0.5f);
        buttonOpenFile.setBackground(new Color(-1180673));
        buttonOpenFile.setForeground(new Color(-4473925));
        buttonOpenFile.setHideActionText(false);
        buttonOpenFile.setHorizontalAlignment(2);
        buttonOpenFile.setMargin(new Insets(3, 8, 3, 8));
        buttonOpenFile.setText("浏览… ");
        buttonOpenFile.setToolTipText("选择要反编译的apk文件");
        panel3.add(buttonOpenFile, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 20, 0), -1, -1));
        panel4.setBackground(new Color(-1180673));
        panelRoot.add(panel4, new CellConstraints(2, 9, 5, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(0, 0, 0, 8)));
        logClear = new JButton();
        logClear.setBackground(new Color(-1180673));
        logClear.setMargin(new Insets(5, 21, 5, 21));
        logClear.setText("清空日志");
        logClear.setToolTipText("清空运行日志");
        panel4.add(logClear, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel4.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelRoot;
    }

}
