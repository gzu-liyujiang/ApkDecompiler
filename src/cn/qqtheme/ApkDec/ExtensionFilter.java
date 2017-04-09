package cn.qqtheme.ApkDec;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Arrays;

/**
 * 按扩展名过滤文件
 * <p>
 * Created by liyujiang on 16-8-12.
 */
public class ExtensionFilter extends FileFilter {
    private String[] types;

    public ExtensionFilter(String[] types) {
        this.types = types;
    }

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        for (String type : types) {
            if (f.getName().toLowerCase().endsWith(type)) {
                return true;
            }
        }
        return false;
    }

    public String getDescription() {
        return "文件类型" + Arrays.toString(types);
    }

}
