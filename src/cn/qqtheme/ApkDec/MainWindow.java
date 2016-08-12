package cn.qqtheme.ApkDec;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

/**
 * Created by liyujiang on 16-8-12.
 */
public class MainWindow {
    private JPanel panelRoot;
    private JTextField inputApk;
    private JButton buttonDecodeXml;
    private JButton buttonDecodeJar;
    private JButton buttonJdGui;
    private JTextArea logArea;
    private JButton buttonOpenFile;
    private String apkPath;

    public MainWindow() {
        buttonOpenFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseApk();
            }
        });
        buttonDecodeXml.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (apkValid()) {

                }
            }
        });
        buttonDecodeJar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (apkValid()) {

                }
            }
        });
        buttonJdGui.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String command = "/home/liyujiang/Apps/ApkDecode/jd-gui/jd-gui";
                try {
                    Runtime.getRuntime().exec(command);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Apk反编译助手");
        URL iconUrl = MainWindow.class.getResource("/icons/liyujiang.png");
        System.out.println(iconUrl);
        frame.setIconImage(new ImageIcon(iconUrl).getImage());
        MainWindow mainWindow = new MainWindow();
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
        JMenuItem menuOpenFIle = new JMenuItem("打开");
        menuOpenFIle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseApk();
            }
        });
        JMenuItem menuAbout = new JMenuItem("关于");
        menuAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(panelRoot, "开发工具：Intellij IDEA 2016.2.1\n\n" +
                        "制作：穿青人@李玉江[QQ:1032694760]");
            }
        });
        menuMain.add(menuOpenFIle);
        menuMain.add(menuAbout);
        menuBar.add(menuMain);
        return menuBar;
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
