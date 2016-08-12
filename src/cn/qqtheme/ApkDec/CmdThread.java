package cn.qqtheme.ApkDec;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by liyujiang on 16-8-12.
 */
public class CmdThread extends Thread {
    private JTextArea logArea;
    private String command;

    public CmdThread(JTextArea logArea, String command) {
        this.logArea = logArea;
        this.command = command;
    }

    @Override
    public void run() {
        logArea.append("执行命令：" + command + "\n");
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logArea.append(line + "\n");
            }
            reader.close();
            JOptionPane.showMessageDialog(logArea.getRootPane(), "反编译成功！");
        } catch (IOException e1) {
            logArea.append("出错了：" + e1 + "\n");
        }
    }

}
