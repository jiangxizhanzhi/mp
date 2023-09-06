package cn.cyberpeace.mp.base.util.mptool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {
    public static List<String> listFileNames(ZipFile zipFile, String dir) {
        if (StrUtil.isNotBlank(dir)) {
            // 目录尾部添加"/"
            dir = StrUtil.addSuffixIfNot(dir, StrUtil.SLASH);
        }

        final List<String> fileNames = new ArrayList<>();
        String name;
        for (ZipEntry entry : Collections.list(zipFile.entries())) {
            name = entry.getName();
            if (StrUtil.isEmpty(dir) || name.startsWith(dir)) {
                final String nameSuffix = StrUtil.removePrefix(name, dir);
                if (StrUtil.isNotEmpty(nameSuffix) && false == StrUtil.contains(nameSuffix, CharUtil.SLASH)) {
                    fileNames.add(nameSuffix);
                }
            }
        }

        return fileNames;
    }
}
