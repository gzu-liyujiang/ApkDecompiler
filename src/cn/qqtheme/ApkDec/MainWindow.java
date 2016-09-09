package cn.qqtheme.ApkDec;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by liyujiang on 16-8-12.
 *
 * @author liyujiang
 */
public class MainWindow {
    private String appPath = Constants.APP_PATH;
    private String toolPath = Constants.TOOL_PATH;
    private JPanel panelRoot;
    private JTextField inputApk;
    private JButton buttonDecodeXml;
    private JButton buttonDecodeJar;
    private JButton buttonJdGui;
    private JTextArea logArea;
    private JButton buttonOpenFile;
    private JButton logClear;
    private String apkPath;

    private MainWindow() {
        logArea.append("\n\n");
        //获取当前执行的jar包的绝对路径
        String path = MainWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        appPath = new java.io.File(path).getParentFile().getAbsolutePath();
        appPath = appPath.replaceAll("\\\\", "/");
        try {
            appPath = java.net.URLDecoder.decode(appPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!appPath.endsWith("/")) {
            appPath += "/";
        }
        toolPath = appPath + "tools/";
        logArea.append("程序包路径为：" + appPath + "\n");
        //This inspection reports all anonymous classes which can be replaced with lambda expressions
        //Lambda syntax is not supported under Java 1.7 or earlier JVMs.
        buttonOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.chooseApk();
            }
        });
        buttonDecodeXml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainWindow.this.apkValid()) {
                    String dirPath = apkPath.replace(".apk", "");
                    String cmd = toolPath + "apktool/apktool --advanced decode --force --output " + dirPath + " --no-src --frame-path " + toolPath + "apktool/ " + apkPath;
                    new CmdThread(logArea, cmd).start();
                }
            }
        });
        buttonDecodeJar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainWindow.this.apkValid()) {
                    String jarPath = apkPath.replace("apk", "jar");
                    String cmd = toolPath + "dex2jar/d2j-dex2jar.sh" + " --force --output " + jarPath + " --print-ir " + apkPath;
                    new CmdThread(logArea, cmd).start();
                }
            }
        });
        buttonJdGui.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainWindow.this.apkValid()) {
                    openJdGui(apkPath.replace("apk", "jar"));
                }
            }
        });
        logClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.setText("欢迎使用♪(^∇^*)使用方法可点击菜单查看帮助。\n\n");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(Constants.APP_NAME + Constants.APP_VERSION);
        MainWindow mainWindow = new MainWindow();
        frame.setIconImage(new ImageIcon(mainWindow.appPath + "icons/default.png").getImage());
        frame.setContentPane(mainWindow.panelRoot);
        frame.setJMenuBar(mainWindow.createMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        frame.setResizable(false);//不允许缩放
        frame.setLocationRelativeTo(null);//居中
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuMain = new JMenu("菜单");
        JMenuItem menuJdGui = new JMenuItem("JD-GUI");
        menuJdGui.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.openJdGui(null);
            }
        });
        JMenuItem menuHelp = new JMenuItem("帮助");
        menuHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.help();
            }
        });
        JMenuItem menuAbout = new JMenuItem("关于");
        menuAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.about();
            }
        });
        menuMain.add(menuJdGui);
        menuMain.add(menuHelp);
        menuMain.add(menuAbout);
        menuBar.add(menuMain);
        return menuBar;
    }

    private void help() {
        JOptionPane.showMessageDialog(panelRoot,
                "1、点击浏览选择一个需要反编译的apk文件；\n" +
                        "2、点击反编译xml以及dex2jar；\n" +
                        "3、点击用jd-gui打开，查看反编译后的jar的源代码。\n",
                "帮助", JOptionPane.QUESTION_MESSAGE,
                new ImageIcon(appPath + "icons/liyujiang.png"));
    }

    private void about() {
        JOptionPane.showMessageDialog(panelRoot,
                "开发工具：Intellij IDEA 2016.2\n" +
                        "测试系统：Ubuntu 16.10 (devel)\n\n" +
                        "制作：穿青人@李玉江[QQ:1032694760]",
                "关于", JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(appPath + "icons/liyujiang.png")
        );
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
            apkPath = apkPath.replaceAll("\\\\", "/");
            inputApk.setText(apkPath);
        }
    }

    private boolean apkValid() {
        if (apkPath == null || apkPath.trim().length() == 0) {
            JOptionPane.showMessageDialog(panelRoot, "请先选择要反编译的apk文件");
            return false;
        }
        return true;
    }

    private void openJdGui(String path) {
        String cmd = toolPath + "jd-gui";
        if (path != null && path.trim().length() > 0) {
            cmd += " " + path;
        }
        logArea.append("执行命令：" + cmd + "\n");
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e1) {
            logArea.append("出错了：" + e1 + "\n");
        }
    }

}
