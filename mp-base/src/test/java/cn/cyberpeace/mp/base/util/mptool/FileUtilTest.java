package cn.cyberpeace.mp.base.util.mptool;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class FileUtilTest {

    @Test
    void writeUtf8Lines() {
        List<String> objects = new ArrayList<String>();
        objects.add("1");
        objects.add("2");
        FileUtil.writeUtf8Lines(objects, "d:/1.txt");
    }

    @Test
    void mkParentDirs() {
        FileUtil.mkParentDirs(new File("d:/1/2/1.txt"));
    }

    @Test
    void listFileNames() {
        System.out.println(FileUtil.listFileNames("d:/document"));
    }

    @Test
    void getAbsolutePath() {
        System.out.println(FileUtil.getAbsolutePath("classpath:config/example.setting"));
    }

    @Test
    void del() {
        FileUtil.del(new File("d:/1.txt"));
    }

    @Test
    void mkdir() {
        FileUtil.mkdir("d:/1");
    }

    @Test
    void getTmpDirPath() {
        System.out.println(FileUtil.getTmpDirPath());

    }

    @Test
    void getUserHomePath() {
        System.out.println(FileUtil.getUserHomePath());

    }

    @Test
    void getParent() {
        System.out.println(FileUtil.getParent("d:/aaa/bbb/cc/ddd", 1));
    }

    @Test
    void getName() {
        System.out.println(FileUtil.getName("d:/aaa/bbb/cc/ddd/1.txt"));
    }

    @Test
    void getInputStream() {
        FileUtil.getInputStream("d:/1.txt");
    }

    @Test
    void readUtf8Lines() {
        System.out.println(FileUtil.readUtf8Lines(new File("d:/1.txt")));
    }

    @Test
    void move() {
        FileUtil.move(new File("d:/1.txt"), new File("d:/1/1.txt"), true);
    }

    @Test
    void getSuffix() {
        System.out.println(FileUtil.getSuffix("d:/1.txt"));
    }

    @Test
    void exist() {
    }
}