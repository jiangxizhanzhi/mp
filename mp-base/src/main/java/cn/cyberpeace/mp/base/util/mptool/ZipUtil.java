package cn.cyberpeace.mp.base.util.mptool;

import lombok.extern.slf4j.Slf4j;

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

/**
 * @ClassName ZipUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class ZipUtil {

    private static final int BUFFER_SIZE = 2 * 1024;

    private static final String SUFFIX = ".zip";

    /**
     * listFileNames
     *
     * @param zipFile
     * @param dir
     * @return
     */
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
     * toZip
     *
     * @param srcDir
     * @return
     */
    public static String toZip(String srcDir) throws RuntimeException {

        File src = new File(srcDir);
        String zipPath = srcDir + File.separator + src.getName() + SUFFIX;
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))){

            File[] listFiles = new File(srcDir).listFiles();
            assert listFiles != null;
            for (File file : listFiles) {
                // 跳过生成的zip文件本身
                if (file.getName().equals(src.getName() + SUFFIX)) {
                    continue;
                }
                compress(file, zos, file.getName());
            }
        } catch (Exception e) {
            log.error("failed to Zip Directory {}", srcDir);
            e.printStackTrace();
        }
        return zipPath;
    }

    /**
     * compress
     *
     * @param sourceFile
     * @param zos
     * @param name
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name)
            throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        // 压缩文件
        if (sourceFile.isFile()) {
            try (FileInputStream in = new FileInputStream(sourceFile)) {
                // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
                zos.putNextEntry(new ZipEntry(name));
                // copy文件到zip输出流中
                int len;
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
            } finally {
                // Complete the entry
                zos.closeEntry();
            }
        } else {
            // 压缩文件夹
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 空文件夹的处理
                zos.putNextEntry(new ZipEntry(name + File.separator));
                // 没有文件，不需要文件的copy
                zos.closeEntry();
            } else {
                for (File file : listFiles) {
                    // 递归写入
                    compress(file, zos, name + File.separator + file.getName());
                }
            }
        }
    }
}
