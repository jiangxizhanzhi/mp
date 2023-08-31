package cn.cyberpeace.mp.base.util.hutool;

import java.io.File;
import java.util.List;

public class FileUtils {

    public static List<String> readUtf8Lines(File file) {
        return FileUtil.readUtf8Lines(file);
    }


}
