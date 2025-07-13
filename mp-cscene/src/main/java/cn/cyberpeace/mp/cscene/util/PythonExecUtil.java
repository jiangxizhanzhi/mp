package cn.cyberpeace.mp.cscene.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

@Slf4j
public final class PythonExecUtil {

    private static String windowsPythonPath = "python.exe";

    private static String linuxPythonPath = "python3" ;

    public static String exec(String scriptPath, String... params) {
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
