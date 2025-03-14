package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.flow.integration.cloud.lincloud.LinyunCloudClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class InitApplicationService implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        // 把代码里面的几个文件，保存到当前运行目录下，以便后续使用
        saveFile("traffic_generators.zip");
        saveFile("protocol_analyse.py");
        saveFile("protocol_filter.py");
        saveFile("editcaptopcap.py");
    }

    public void saveFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream(fileName);
        writeFile(inputStream, fileName);
    }

    public static void writeFile(InputStream inputStream, String fileName) {

        try(FileOutputStream outputStream = new FileOutputStream(fileName)) {
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
