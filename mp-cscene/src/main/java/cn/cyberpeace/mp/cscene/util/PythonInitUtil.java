package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.cscene.controller.CsceneOutController;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 初始化一些python脚本，将脚本从jar里面拷贝到jar的同级目录下
 */
@Slf4j
public class PythonInitUtil {

    static {
        init();
    }

    public static void init() {
        saveFile("pingtest.py");
        saveFile("ssh_exec.py");
    }

    public static void saveFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        InputStream inputStream = CsceneOutController.class.getClassLoader().getResourceAsStream(fileName);
        writeFile(inputStream, fileName);
    }

    public static void writeFile(InputStream inputStream, String fileName) {

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            if (inputStream == null) {
                System.out.println("文件未找到！");
                return;
            }
            // 输出文件路径
            ;

            // 创建缓冲区（1KB）
            byte[] buffer = new byte[1024];
            int bytesRead;

            // 从 InputStream 读取并写入文件
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            log.info("文件写入成功！");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
