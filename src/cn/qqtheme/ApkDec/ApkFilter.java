package cn.qqtheme.ApkDec;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by liyujiang on 16-8-12.
 */
public class ApkFilter extends FileFilter {

    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith("apk");
    }

    public String getDescription() {
        return "安卓安装包(.apk)";
    }

}
