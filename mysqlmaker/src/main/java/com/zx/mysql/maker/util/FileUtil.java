package com.zx.mysql.maker.util;

import java.io.File;

/**
 * @author zx
 */
public class FileUtil {

    public static File getFile(String parent, String... childs) {
        return getFile(true, parent, childs);
    }

    public static File getFile(boolean isMkParentDirs, String parent, String... childs) {
        StringBuilder sb = new StringBuilder();
        for (String child : childs) {
            sb.append(File.separatorChar).append(child);
        }
        File file = new File(parent, sb.toString());
        if (isMkParentDirs) {
            mkParentDirs(file);
        }
        return file;
    }

    public static void mkParentDirs(File file) {
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
    }

}
