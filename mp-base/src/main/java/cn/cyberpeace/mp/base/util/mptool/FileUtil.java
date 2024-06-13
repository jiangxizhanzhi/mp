package cn.cyberpeace.mp.base.util.mptool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarFile;

@Slf4j
public class FileUtil {

    public static String FILE_SEPARATOR = File.separator;
    public static final String JAR_PATH_EXT = ".jar!";
    public static final String JAR_FILE_EXT = FileNameUtil.EXT_JAR;


    /**
     * 将列表写入文件，覆盖模式，编码为UTF-8
     * Params:
     * list – 列表
     * path – 绝对路径
     * Type parameters:
     * <T> – 集合元素类型
     */
    public static <T> void writeUtf8Lines(Collection<T> list, String filePath) {
        if (null == filePath) {
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8)) {
            for (T line : list) {
                if (null != line) {
                    writer.write(line.toString());
                    writer.newLine();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 创建父文件夹，如果存在直接返回此文件夹
     * Params:
     * path – 文件夹路径，使用POSIX格式，无论哪个平台
     */
    public static File mkParentDirs(String path) {
        if (path == null) {
            return null;
        }
        return mkParentDirs(file(path));
    }

    public static File file(String path) {
        if (null == path) {
            return null;
        }
        return new File(getAbsolutePath(path));
    }

    public static File mkParentDirs(File file) {
        if (null == file) {
            return null;
        }
        return mkdir(file.getParentFile());
    }

    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (false == dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return dir;
    }


    /**
     * 获得指定目录下所有文件 不会扫描子目录
     * Params:
     * path – 相对ClassPath的目录或者绝对路径目录
     */
    public static List<String> listFileNames(String path) {
        if (path == null) {
            return new ArrayList<>(0);
        }
        int index = path.lastIndexOf(FileUtil.JAR_PATH_EXT);
        if (index < 0) {
            // 普通目录
            final List<String> paths = new ArrayList<>();
            final File[] files = ls(path);
            for (File file : files) {
                if (file.isFile()) {
                    paths.add(file.getName());
                }
            }
            return paths;
        }

        // jar文件
        path = getAbsolutePath(path);
        // jar文件中的路径
        index = index + FileUtil.JAR_FILE_EXT.length();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(path.substring(0, index));
            // 防止出现jar!/cn/hutool/这类路径导致文件找不到
            return ZipUtil.listFileNames(jarFile, StrUtil.removePrefix(path.substring(index + 1), "/"));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        } finally {
            IoUtil.close(jarFile);
        }
    }

    public static File[] ls(String path) {
        if (path == null) {
            return null;
        }

        File file = file(path);
        if (file.isDirectory()) {
            return file.listFiles();
        }
        return null;
    }

    /**
     * 获取绝对路径，相对于ClassPath的目录 如果给定就是绝对路径，则返回原路径，原路径把所有\替换为/ 兼容Spring风格的路径表示，例如：classpath:config/example.setting也会被识别后转换
     * Params:
     * path – 相对路径
     */
    public static String getAbsolutePath(String filePath) {
        return getAbsolutePath(filePath, null);
    }

    public static String getAbsolutePath(String path, Class<?> baseClass) {
        String normalPath;
        if (path == null) {
            normalPath = StrUtil.EMPTY;
        } else {
            normalPath = normalize(path);
            if (isAbsolutePath(normalPath)) {
                // 给定的路径已经是绝对路径了
                return normalPath;
            }
        }

        // 相对于ClassPath路径
        final URL url = ResourceUtil.getResource(normalPath, baseClass);
        if (null != url) {
            // 对于jar中文件包含file:前缀，需要去掉此类前缀，在此做标准化，since 3.0.8 解决中文或空格路径被编码的问题
            return FileUtil.normalize(URLUtil.getDecodedPath(url));
        }

        // 如果资源不存在，则返回一个拼接的资源绝对路径
        final String classPath = ClassUtil.getClassPath();
        if (null == classPath) {
            // throw new NullPointerException("ClassPath is null !");
            // 在jar运行模式中，ClassPath有可能获取不到，此时返回原始相对路径（此时获取的文件为相对工作目录）
            return path;
        }

        // 资源不存在的情况下使用标准化路径有问题，使用原始路径拼接后标准化路径
        return normalize(classPath.concat(Objects.requireNonNull(path)));
    }

    public static boolean isAbsolutePath(String path) {
        if (StrUtil.isEmpty(path)) {
            return false;
        }

        // 给定的路径已经是绝对路径了
        return StrUtil.C_SLASH == path.charAt(0) || path.matches("^[a-zA-Z]:([/\\\\].*)?");
    }

    public static String normalize(String path) {
        if (path == null) {
            return null;
        }


        // 兼容Spring风格的ClassPath路径，去除前缀，不区分大小写
        String pathToUse = StrUtil.removePrefixIgnoreCase(path, URLUtil.CLASSPATH_URL_PREFIX);
        // 去除file:前缀
        pathToUse = StrUtil.removePrefixIgnoreCase(pathToUse, URLUtil.FILE_URL_PREFIX);

        // 识别home目录形式，并转换为绝对路径
        if (StrUtil.startWith(pathToUse, '~')) {
            pathToUse = getUserHomePath() + pathToUse.substring(1);
        }

        // 统一使用斜杠
        pathToUse = pathToUse.replaceAll("[/\\\\]+", StrUtil.SLASH).trim();
        //兼容Windows下的共享目录路径（原始路径如果以\\开头，则保留这种路径）
        if (path.startsWith("\\\\")) {
            pathToUse = "\\" + pathToUse;
        }

        String prefix = "";
        int prefixIndex = pathToUse.indexOf(StrUtil.COLON);
        if (prefixIndex > -1) {
            // 可能Windows风格路径
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (StrUtil.startWith(prefix, StrUtil.C_SLASH)) {
                // 去除类似于/C:这类路径开头的斜杠
                prefix = prefix.substring(1);
            }
            if (false == prefix.contains(StrUtil.SLASH)) {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            } else {
                // 如果前缀中包含/,说明非Windows风格path
                prefix = StrUtil.EMPTY;
            }
        }
        if (pathToUse.startsWith(StrUtil.SLASH)) {
            prefix += StrUtil.SLASH;
            pathToUse = pathToUse.substring(1);
        }

        List<String> pathList = StrUtil.split(pathToUse, StrUtil.C_SLASH);
        List<String> pathElements = new LinkedList<>();
        int tops = 0;

        String element;
        for (int i = pathList.size() - 1; i >= 0; i--) {
            element = pathList.get(i);
            // 只处理非.的目录，即只处理非当前目录
            if (false == StrUtil.DOT.equals(element)) {
                if (StrUtil.DOUBLE_DOT.equals(element)) {
                    tops++;
                } else {
                    if (tops > 0) {
                        // 有上级目录标记时按照个数依次跳过
                        tops--;
                    } else {
                        // Normal path element found.
                        pathElements.add(0, element);
                    }
                }
            }
        }

        return prefix + StringUtils.join(pathElements, StrUtil.SLASH);
    }

    /**
     * 删除文件或者文件夹 路径如果为相对路径，会转换为ClassPath路径！ 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹 某个文件删除失败会终止删除操作
     * Params:
     * fullFileOrDirPath – 文件或者目录的路径
     */
    public static void del(String filePath) {
        del(file(filePath));
    }

    public static boolean del(File file) {
        if (file == null || false == file.exists()) {
            // 如果文件不存在或已被删除，此处返回true表示删除成功
            return true;
        }

        if (file.isDirectory()) {
            // 清空目录下所有文件和目录
            boolean isOk = clean(file);
            if (false == isOk) {
                return false;
            }
        }

        // 删除文件或清空后的目录
        return file.delete();
    }

    public static boolean clean(File directory) {
        if (directory == null || directory.exists() == false || false == directory.isDirectory()) {
            return true;
        }

        final File[] files = directory.listFiles();
        if (null != files) {
            boolean isOk;
            for (File childFile : files) {
                isOk = del(childFile);
                if (isOk == false) {
                    // 删除一个出错则本次删除任务失败
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 创建文件夹，如果存在直接返回此文件夹 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     * Params:
     * dirPath – 文件夹路径，使用POSIX格式，无论哪个平台
     */
    public static File mkdir(String dirPath) {
        if (dirPath == null) {
            return null;
        }
        final File dir = file(dirPath);
        return mkdir(dir);
    }

    public static String getTmpDirPath() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }

    /**
     * 获取指定层级的父路径
     * getParent("d:/aaa/bbb/cc/ddd", 0) -》 "d:/aaa/bbb/cc/ddd"
     * getParent("d:/aaa/bbb/cc/ddd", 2) -》 "d:/aaa/bbb"
     * getParent("d:/aaa/bbb/cc/ddd", 4) -》 "d:/"
     * getParent("d:/aaa/bbb/cc/ddd", 5) -》 null
     * <p>
     * Params:
     * filePath – 目录或文件路径
     * level – 层级
     */
    public static String getParent(String filePath, int level) {
        final File parent = getParent(file(filePath), level);
        try {
            return null == parent ? null : parent.getCanonicalPath();
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static File getParent(File file, int level) {
        if (level < 1 || null == file) {
            return file;
        }

        File parentFile = null;
        try {
            parentFile = file.getCanonicalFile().getParentFile();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        if (1 == level) {
            return parentFile;
        }
        return getParent(parentFile, level - 1);
    }

    /**
     * 返回文件名
     * Params:
     * filePath – 文件
     */
    public static String getName(String filePath) {
        if (null == filePath) {
            return null;
        }
        int len = filePath.length();
        if (0 == len) {
            return filePath;
        }
        if (CharUtil.isFileSeparator(filePath.charAt(len - 1))) {
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (CharUtil.isFileSeparator(c)) {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * 获得输入流
     * Params:
     * path – 文件路径
     */
    public static InputStream getInputStream(String filePath) {
        File file = new File(filePath);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 按行处理文件内容，编码为UTF-8
     * Params:
     * file – 文件
     * lineHandler – LineHandler行处理器
     */
    public static List<String> readUtf8Lines(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return lines;
    }


    /**
     * 移动文件或目录 当目标是目录时，会将源文件或文件夹整体移动至目标目录下
     * Params:
     * src – 源文件或目录路径
     * target – 目标路径，如果为目录，则移动到此目录下
     * isOverride – 是否覆盖目标文件
     */
    public static void move(File src, File target, boolean isOverride) {
        move(src.toPath(), target.toPath(), isOverride);
    }

    public static Path move(Path src, Path target, boolean isOverride) {
        final CopyOption[] options = isOverride ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[]{};
        if (isDirectory(target, false)) {
            target = target.resolve(src.getFileName());
        }
        // 自动创建目标的父目录
        mkParentDirs(target);
        try {
            return Files.move(src, target, options);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static Path mkParentDirs(Path path) {
        return mkdir(path.getParent());
    }

    public static Path mkdir(Path dir) {
        if (null != dir && false == exists(dir, false)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                log.error(e.getMessage());
                return null;
            }
        }
        return dir;
    }

    public static boolean exists(Path path, boolean isFollowLinks) {
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        return Files.exists(path, options);
    }

    public static boolean isDirectory(Path path, boolean isFollowLinks) {
        if (null == path) {
            return false;
        }
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        return Files.isDirectory(path, options);
    }

    /**
     * 获取文件后缀名，扩展名不带“.”
     *
     * @param fileName 文件
     */
    public static String getSuffix(String fileName) {
        return FileNameUtil.getSuffix(fileName);
    }

    /**
     判断文件是否存在，如果path为null，则返回false
     Params:
     path – 文件路径
     */
    public static boolean exist(String path) {
        return (null != path) && file(path).exists();
    }


    public static void setResponse(File file, Boolean delete, HttpServletResponse response) {
        // 以流的形式下载文件。
        InputStream fis = null;
        OutputStream outputStream = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);

            // 清空response
            response.reset();

            // 设置返回
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(file.getName(),"UTF-8").replaceAll("\\+","%20"));
            outputStream = response.getOutputStream();
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            log.error("setResponse error:", e);
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error("setResponse error:", e);
            }

            if (delete.equals(Boolean.TRUE) && null != file && file.exists()) {
                file.delete();
            }
        }
    }


}
