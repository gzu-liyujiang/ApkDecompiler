/*
 * Copyright (c) 2019. Li Yujiang, Chuanqing People. All rights reserved.
 */

package cn.qqtheme.ApkDec;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by liyujiang on 2016-8-12.
 *
 * @author 李玉江[1032694760@qq.com]
 */
public class CmdThread extends Thread {
    private JTextArea logArea;
    private String command;

    CmdThread(String command) {
        this.logArea = MainWindow.getInstance().getLogArea();
        this.command = command;
    }

    @Override
    public void run() {
        MainWindow.getInstance().showLog("执行命令：" + command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    MainWindow.getInstance().showLog(line);
                }
            } catch (Exception e) {
                MainWindow.getInstance().showLog("出错了：" + e);
                System.exit(0);
            }
            reader.close();
            if (command.contains("apktool") || command.contains("dex2jar")) {
                MainWindow.getInstance().showLog("反编译结束！\n");
            }
            if (command.contains("apktool")) {
                MainWindow.getInstance().openDir();
            } else if (command.contains("dex2jar")) {
                MainWindow.getInstance().openJdGui();
            }
        } catch (Exception e) {
            MainWindow.getInstance().showLog("出错了：" + e);
            System.exit(0);
        }
    }

}
