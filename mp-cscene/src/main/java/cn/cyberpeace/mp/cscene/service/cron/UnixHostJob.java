package cn.cyberpeace.mp.cscene.service.cron;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.hutool.core.io.FileUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/6/19 14:07
 */
@Slf4j
@Component
public class UnixHostJob {

    @Resource(name = "logRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    static {
        String dir = BeanCommon.getBean(BaseAppConfig.class).getResourcesDir();
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        saveFile("ssh_run_cmd.py", dir + "/ssh_run_cmd.py");
        saveFile("ssh_run_cmd2.py", dir + "/ssh_run_cmd2.py");
        saveFile("ssh_run_cmd3.py", dir + "/ssh_run_cmd3.py");
        saveFile("host_info.json", dir + "/host_info.json");
    }

    private static void saveFile(String from, String to) {
        // 已存在 跳过
        File file = new File(to);
        if (file.exists()) {
            return;
        }
        InputStream inputStream = UnixHostJob.class.getClassLoader().getResourceAsStream(from);
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

    private Integer[] runningEvent = new Integer[]{SceneElementModel.Event.UNPAUSE, SceneElementModel.Event.RECREATE, SceneElementModel.Event.CREATE, SceneElementModel.Event.MODIFY, SceneElementModel.Event.RESUME};

    @Scheduled(cron = "27 0/3 * * * ?")
    public void execute() {

        SceneElementMapper sceneElementMapper = BeanCommon.getBean(SceneElementMapper.class);
        var list = sceneElementMapper.list(new HumanCondition().filterIn(SceneElementModel::getSystemType, new String[]{"201", "202", "203", "204"})
                                                   .filterIn(SceneElementModel::getEvent, runningEvent));
        if (CollectionUtils.isEmpty(list)) {
            log.info("未查询到unix节点");
            return;
        } else {
            log.info("查询到 {} 个unix节点", list.size());
        }

        // 读取wazuh数据模板
        String dir = BeanCommon.getBean(BaseAppConfig.class).getResourcesDir();
        String filePath = dir + "/host_info.json";
        var jsonStr = FileUtil.readString(new File(filePath), "utf-8");
        Map<String, Object> jsonContent2 = JsonCommon.parseMap(jsonStr);

        // 循环处理
        for (var ele : list) {
            log.info("处理节点 {} , name {}", ele.getId(), ele.getName());
            String sceneId = ele.getScene();
            var sceneHandler = new SceneHandler(sceneId, null, false, new SceneContextBO());
            CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
            var e = pluginHandler.getElement(ele.getNodeId());
            if (e.getSrcElement().getPorts() == null) {
                log.info("端口不存在，跳过");
                continue;
            }
            String ip = null;
            Integer port = null;
            String usename = null;
            String password = null;
            String portIp = null;
            for (var p : e.getSrcElement().getPorts()) {
                if (StringUtils.isNotEmpty(ip)) {
                    break;
                }
                var pluginPort = pluginHandler.getPort(p.getId());
                if (pluginPort.getAccessModes() == null) {
                    continue;
                }
                for (var a : pluginPort.getAccessModes()) {
                    if (AccessMode.Protocol.SSH.equals(a.getProtocol())) {
                        ip = a.getAccessIp();
                        port = a.getAccessPort();
                        usename = a.getUsername();
                        password = a.getPassword();
                        portIp = p.getIp();
                        break;
                    }
                }
            }
            if (StringUtils.isEmpty(ip)) {
                log.info("未发现ssh协议，跳过");
                continue;
            }
            var info = getData(dir + "/ssh_run_cmd.py", ip, port, usename, password);
            if (info == null) {
                log.info("未采集到信息");
            } else {
                // 向 es 发送构造的数据
                Map<String, Object> jsonContent = JsonCommon.parseMap(jsonStr);
                var sicLaunchOutput = ((Map) ((Map) jsonContent2.get("data")).get("sic_launch_output"));
                sicLaunchOutput.put("CPUPercent", String.valueOf(info.getCpu()));
                sicLaunchOutput.put("MemoryUsedPercent", String.valueOf(info.getRam()));
                sicLaunchOutput.put("DiskUsed", String.valueOf(info.getDiskUsed()));
                sicLaunchOutput.put("DiskTotal", String.valueOf(info.getDiskAll()));
                ((Map) jsonContent.get("data")).put("sic_launch_output", sicLaunchOutput);

                var agent = (Map) jsonContent.get("agent");
                agent.put("name", e.getSrcElement().getEid());
                agent.put("ip", portIp);
                jsonContent.put("agent", agent);

                String t1 = TimeUtils.localDateTimeToString(LocalDateTime.now(), TimeUtils.tzMillsDateTimeFormatter);
                String t2 = t1.replace("Z", "+0800");
                jsonContent.put("@timestamp", t1);
                jsonContent.put("timestamp", t2);

                var esIndex = DataCollectionService.buildRequestIndex(LocalDateTime.now(), LocalDateTime.now())[0];

                IndexRequest request = new IndexRequest(esIndex).source(JsonCommon.stringify(jsonContent), XContentType.JSON);
                try {
                    restHighLevelClient.index(request, RequestOptions.DEFAULT);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private Info getData(String scriptPath, String host, Integer port, String username, String password) {
        BufferedReader input = null;
        try {

            String os = System.getProperty("os.name").toLowerCase();
            String python_exec = "python3 %s %s %s %s %s";
            if (os.contains("win")) {
                python_exec = "C:\\python3\\python.exe %s %s %s %s %s";
            }

            String pythonCmd = String.format(python_exec, scriptPath, host, port, username, password);
            log.info("============python analyse cmd: {}", pythonCmd);
            Process process = Runtime.getRuntime().exec(pythonCmd);
            process.waitFor(5, TimeUnit.SECONDS);
            String res = "";
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                res = line;
            }
            log.info("result:{}", res);
            Info da = JsonCommon.snakeParse(res, Info.class);
            return da;
        } catch (Exception e) {
            log.error("exec error:", e);
            return null;
        }
    }

    @Getter
    @Setter
    public static class Info {
        private Double cpu;
        private Double ram;
        private Double diskAll;
        private Double diskUsed;
    }
}
