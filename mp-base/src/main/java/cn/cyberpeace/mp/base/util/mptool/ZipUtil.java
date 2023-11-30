package cn.cyberpeace.mp.base.util.mptool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Slf4j
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

    /**
     * 把 sourceDir 下的所有目录和文件 压缩到 zipFile里面
     *
     * @param sourceDir
     * @param zipFile
     */
    public static void zipDirectory(File sourceDir, File zipFile) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);
            compressDirectory("", sourceDir, zos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            try {
                fos.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public static void compressDirectory(String baseDirPath, File sourceDir, ZipOutputStream zos) {
        File[] files = sourceDir.listFiles();
        for (var file : files) {
            if (file.isDirectory()) {
                if (StringUtils.isNotEmpty(baseDirPath)) {
                    compressDirectory(baseDirPath + "/" + sourceDir.getName(), file, zos);
                } else {
                    compressDirectory(sourceDir.getName(), file, zos);
                }
            } else {
                compressFile(baseDirPath, file, zos);
            }
        }
    }


    /**
     * 把 baseDirPath 下的 file 压缩到 zos 里面
     *
     * @param baseDirPath
     * @param file
     * @param zos
     */
    private static void compressFile(String baseDirPath, File file, ZipOutputStream zos) {
        byte[] buffer = new byte[1024 * 10];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            var zipEntryName = file.getName();
            if (StringUtils.isNotEmpty(baseDirPath)) {
                zipEntryName = baseDirPath + "/" + zipEntryName;
            }
            zos.putNextEntry(new ZipEntry(zipEntryName));
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                zos.closeEntry();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                fis.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
