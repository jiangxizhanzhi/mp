package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

@Slf4j
@Component
public final class PythonExecUtil {

    private static String windowsPythonPath = "python.exe";

    private static String linuxPythonPath = "python3" ;

    private static volatile boolean INIT = false ;

    private static void init(){
        if (INIT) {
            return;
        }
        log.info("init pingtest.py 、 ssh_exec.py start");
        String dir = BeanCommon.getBean(BaseAppConfig.class).getResourcesDir();
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        saveFile("pingtest.py", dir + "pingtest.py");
        saveFile("ssh_exec.py", dir + "ssh_exec.py");
        log.info("init pingtest.py 、 ssh_exec.py end");
        INIT = true;
    }

    private static void saveFile(String from, String to) {
        // 已存在 跳过
        File file = new File(to);
        if (file.exists()) {
            return;
        }
        InputStream inputStream = PythonExecUtil.class.getClassLoader().getResourceAsStream(from);
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

    public String exec(String scriptPath, String... params) {
        String paramsStr = "";
        if (params != null) {
            for (String p : params) {
                paramsStr += "\"" + p + "\" ";
            }
        }

        String os = System.getProperty("os.name").toLowerCase();
        String pythonPath = os.contains("windows") ? windowsPythonPath : linuxPythonPath;

        String pythonCmd = String.format("%s %s %s", pythonPath, scriptPath, paramsStr);
        log.info("============python exec cmd: {}", pythonCmd);
        OutputStream out = null;
        BufferedReader input = null;
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(pythonCmd);
            process.waitFor();
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                result = line;
            }
            log.info("============ result: {}", result);
        } catch (Exception e) {
            log.error("failed to exec python cmd ", e);
        }
        return result;
    }
}
