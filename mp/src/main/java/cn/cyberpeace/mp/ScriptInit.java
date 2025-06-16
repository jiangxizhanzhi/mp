package cn.cyberpeace.mp;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ScriptInit {

    /**
     * 将jar包中的脚本文件复制到mp容器的指定目录下
     * 如果不存在才复制 , 已存在的不处理。这样可以比较灵活。
     * 1. 如果jar包中的脚本文件变了，可以删除指定目录下的对应脚本，然后更新mp
     * 2. 如果jar包不动，想要改变脚本文件，可以直接替换指定目录下的对应脚本
     */
    public static void init() {
        // 复制 自定义 探针脚本，到 scene_plugin_collection/init_custom 目录下
        copyInitCustomScript();
        // 复制 容器靶标参数的 python解析脚本，到 scene_plugin_cloud/init_custom 目录下 , 不需要上传到mp的本地文件服务
        copyDevopsParser();
    }

    /**
     * 复制 容器靶标参数的 python解析脚本，到 scene_plugin_cloud/init_custom 目录下
     */
    private static void copyDevopsParser(){
        String dir = BeanCommon.getBean(BaseAppConfig.class).getResourcesDir() + "/scene_plugin_cloud/parser";
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        saveFile("script/parser/devops_parser.py", dir + "/devops_parser.py");
    }

    /**
     * 复制 自定义 探针脚本，到 scene_plugin_collection/init_custom 目录下
     */
    private static void copyInitCustomScript() {
        String dir = BeanCommon.getBean(BaseAppConfig.class).getResourcesDir() + "/scene_plugin_collection/init_custom";
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }

        saveFile("script/init_custom/get_hostname.bat", dir + "/get_hostname.bat");
        saveFile("script/init_custom/get_hostname.sh", dir + "/get_hostname.sh");
        saveFile("script/init_custom/get_hostname.py", dir + "/get_hostname.py");

        saveFile("script/init_custom/get_ip.bat", dir + "/get_ip.bat");
        saveFile("script/init_custom/get_ip.sh", dir + "/get_ip.sh");
        saveFile("script/init_custom/get_ip.py", dir + "/get_ip.py");
    }

    private static void saveFile(String from, String to) {
        // 已存在 跳过
        File file = new File(to);
        if (file.exists()) {
            return;
        }
        InputStream inputStream = ScriptInit.class.getClassLoader().getResourceAsStream(from);
        writeFile(inputStream, to);
    }

    private static void writeFile(InputStream inputStream, String fileName) {

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
