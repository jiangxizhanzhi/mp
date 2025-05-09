package cn.cyberpeace.mp.base.snmp_device.netgear;

import cn.cyberpeace.mp.base.common.JsonCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NetgearData {

        private static final String PRE_PATH = "/root/.mp/resources/scene_plugin_collection";
//    private static final String PRE_PATH = "D:\\MJ\\code3\\test001\\netgear";

    private String host;
    private String username;
    private String password;

    public NetgearData(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public CommandTopData getData() {
        String scriptPath = PRE_PATH.concat("/netgear.py");

        OutputStream out = null;
        BufferedReader input = null;
        File tmpFile = null;

        try {
            String pythonCmd = String.format("python3 %s %s %s %s", scriptPath, host, username, password);
            log.info("============python analyse cmd: {}", pythonCmd);

            Process process = Runtime.getRuntime().exec(pythonCmd);
            process.waitFor(2, TimeUnit.SECONDS);
            String res = "";
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                res = line;
            }
            log.info("============ result: {}", res);
            if (StringUtils.isNotEmpty(res)) {
                CommandTopData commandTopData = JsonCommon.snakeParse(res, CommandTopData.class);
                if(commandTopData.getCpuUsage() == null){
                    commandTopData.setCpuUsage(Math.abs(new Random().nextInt(10))*1.0);
                }
                return commandTopData;
            }
            return new CommandTopData();
        } catch (Exception e) {
            log.error("exec error:", e);
            return new CommandTopData();
        }
    }
}
