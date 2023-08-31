package cn.cyberpeace.mp.base.util.hutool;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static String FILE_SEPARATOR = File.separator;


    /**
     * 将集合中的字符串写入文件
     *
     * @param lines
     * @param filePath
     */
    public static void writeUtf8Lines(List<String> lines, String filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 创建文件之前，自动创建其父级目录
     *
     * @param filePath
     */
    public static void mkParentDirs(String filePath) {
        File parent = new File(filePath).getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    /**
     * 列出指定目录下的所有文件名
     *
     * @param path
     * @return
     */
    public static List<String> listFileNames(String path) {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName());
                    }
                }
            }
        }
        return fileNames;
    }

    /**
     * 输出文件的绝对路径
     *
     * @param filePath
     * @return
     */
    public static String getAbsolutePath(String filePath) {
        File file = new File(filePath);
        return file.getAbsolutePath();
    }

    /**
     * 删除文件或目录
     *
     * @param filePath
     */
    public static void del(String filePath) {
        Path path = Paths.get(filePath);
        try {
            Files.delete(path);
        } catch (IOException e) {

        }
    }

    /**
     * 创建目录
     *
     * @param filePath
     * @return
     */
    public static boolean mkdir(String filePath) {
        return new File(filePath).mkdirs();
    }

    public static String getTmpDirPath() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }

    /**
     * 获取指定层级的父路径
     *
     * @param filePath
     * @param level
     * @return
     */
    public static String getParent(String filePath, int level) {
        File file = new File(filePath);
        if (!file.isAbsolute()) {
            file = file.getAbsoluteFile();
        }
        String parentPath = file.getAbsolutePath();
        if (level <= 0) {
            return parentPath;
        }
        for (int i = 0; i < level && parentPath != null; i++) {
            File parent = new File(parentPath);
            parentPath = parent.getParent();
        }
        return parentPath;
    }

    /**
     * 获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getName(String filePath) {
        if (filePath == null) {
            return null;
        }
        File file = new File(filePath);
        String fileName = file.getName();
        return fileName;
    }

    public static InputStream getInputStream(String filePath) {
        File file = new File(filePath);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public static List<String> readUtf8Lines(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
        }
        return lines;
    }


    /**
     * 移动文件
     *
     * @param src
     * @param target
     * @param isOverride 是否重写
     */
    public static void move(File src, File target, boolean isOverride) {
        if (target.exists() && !isOverride) {
        } else {
            try {
                Files.move(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
            }
        }
    }

    /**
     * 获取文件后缀
     *
     * @param localFilepath
     * @return
     */
    public static String getSuffix(String localFilepath) {
        int dotIndex = localFilepath.lastIndexOf(".");
        return (dotIndex == -1) ? "" : localFilepath.substring(dotIndex + 1);
    }

    public static boolean exist(String filePath) {
        return new File(filePath).exists();
    }


}
