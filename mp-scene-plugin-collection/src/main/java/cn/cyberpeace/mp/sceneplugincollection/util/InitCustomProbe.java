package cn.cyberpeace.mp.sceneplugincollection.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InitCustomProbe {
    public static void init() {
        String dir = BeanCommon.getBean(BaseAppConfig.class).getResourcesDir() + "/scene_plugin_collection/init_custom";
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        saveFile("init_custom/get_hostname.bat", dir + "/get_hostname.bat");
        saveFile("init_custom/get_hostname.sh", dir + "/get_hostname.sh");
        saveFile("init_custom/get_hostname.py", dir + "/get_hostname.py");

        saveFile("init_custom/get_ip.bat", dir + "/get_ip.bat");
        saveFile("init_custom/get_ip.sh", dir + "/get_ip.sh");
        saveFile("init_custom/get_ip.py", dir + "/get_ip.py");
    }

    public static void saveFile(String from, String to) {
        File file = new File(to);
        if (file.exists()) {
            file.delete();
        }
        InputStream inputStream = InitCustomProbe.class.getClassLoader().getResourceAsStream(from);
        writeFile(inputStream, to);
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
