package cn.cyberpeace.mp.job.init;


import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.scene.model.ArkimeFlowPersistenceTaskModel;
import cn.cyberpeace.mp.scene.repository.ArkimeFlowPersistenceTaskMapper;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.FlowPersistenceRule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.cyberpeace.mp.cscene.controller.FlowMonitorController.SCENE_TO_FILE_QUEUE_MAP;

@Slf4j
@Component
public class ArkimeFlowPersistenceTaskJob {

    @Autowired
    private ArkimeFlowPersistenceTaskMapper arkimeFlowPersistenceTaskMapper;
    @Autowired
    private SysConfigService sysConfigService;


    private static final String FILE_FOLDER = "/root/.mp/files";

    private static final String ARKIME_STATS = "/api/stats";

    private static final ScheduledThreadPoolExecutor SCHEDULER_EXECUTOR = new ScheduledThreadPoolExecutor(1, r -> {
        Thread t = new Thread(r);
        t.setName("arkime-flow-persistence-task");
        return t;
    }, new ThreadPoolExecutor.AbortPolicy());

    @MpInitiate(id = "arkime_flow_persistence_task", description = "对arkime采集到的流量数据进行留存")
    public void execute() {
        SCHEDULER_EXECUTOR.scheduleWithFixedDelay(() -> {
            //新增文件
            downloadPcapFile();
            //将文件移除，保持在限制的最大保留时间
            removeFileToMaxLimit();
            //清楚场景关闭的queue
            gcQueue();
        }, 1, 5, TimeUnit.MINUTES);
    }

    private void gcQueue() {
        Set<String> keys = SCENE_TO_FILE_QUEUE_MAP.keySet();
        if (CollectionUtils.isNotEmpty(keys)) {
            var invalidPersistenceTasks = arkimeFlowPersistenceTaskMapper.list(new HumanCondition().
                    filterEqual(ArkimeFlowPersistenceTaskModel::getValid, false)
                    .filterIn(ArkimeFlowPersistenceTaskModel::getScene, keys.toArray(new String[0])));
            if (CollectionUtils.isNotEmpty(invalidPersistenceTasks)) {
                invalidPersistenceTasks.stream().map(ArkimeFlowPersistenceTaskModel::getScene).forEach(SCENE_TO_FILE_QUEUE_MAP::remove);
            }

        }
    }

    private void downloadPcapFile()  {
        var persistenceTasks = arkimeFlowPersistenceTaskMapper.list(new HumanCondition().filterEqual(ArkimeFlowPersistenceTaskModel::getValid, true));
        for (ArkimeFlowPersistenceTaskModel persistenceTask : persistenceTasks) {
            HttpClient httpClient = HttpClient.builder()
                    .configBasicAuth("admin", "admin")
                    .configMaxConnPerRoute(1)
                    .configMaxConnTotal(1)
                    .configServer("http://" + persistenceTask.getArkimeIp() + ":8005")
                    .configTimeout(20000)
                    .build();
            long currentTIme = arkimeCurrentTime(httpClient);
            downloadRecentPcapFile(httpClient, persistenceTask, currentTIme);
        }


    }



    private long arkimeCurrentTime(HttpClient httpClient) {

        String result = httpClient.get("/api/stats", null, Collections.emptyMap(), 5000)
                .getResult();
        return JsonCommon.parse(result, JsonNode.class).get("data").
        get(0).get("currentTime").asLong();
    }

    private void removeFileToMaxLimit() {
        int maxLimit = sysConfigService.getFlowPersistenceTimeConfig() / 5;
        SCENE_TO_FILE_QUEUE_MAP.forEach((sceneId, queue) -> {
            while (queue.size() > maxLimit) {
                removeTheOldestFile(sceneId);
            }
        });
    }


    private void removeTheOldestFile(String sceneId) {
        if (Objects.nonNull(SCENE_TO_FILE_QUEUE_MAP.get(sceneId))) {
            Path oldestPath = SCENE_TO_FILE_QUEUE_MAP.get(sceneId).poll();
            if (Objects.nonNull(oldestPath)) {
                try {
                    Files.deleteIfExists(oldestPath);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

    }


    private void downloadRecentPcapFile(HttpClient httpClient, ArkimeFlowPersistenceTaskModel persistenceTask, long currentTime) {
        OutputStream out = null;
        try {
            Path directParentPath = Path.of(FILE_FOLDER, persistenceTask.getScene());
            if (Files.notExists(directParentPath)) {
                Files.createDirectories(directParentPath);
            }
            String fileName = sysConfigService.getFlowPersistenceTimestamp() + "_" + "flow_pcap.pcap";
            Path filePath = null;
            if (Files.notExists(Path.of(directParentPath.toString(), fileName))) {
                filePath = Files.createFile(Path.of(directParentPath.toString(), fileName));
            }
            if (Objects.nonNull(filePath)) {
                //固定5分钟
                long startTime = currentTime - 300;

                String url = "/api/sessions/pcap?"+"startTime="+ startTime
                        + "&" + "stopTime=" + currentTime;
                if (StringUtils.isNotEmpty(persistenceTask.getFlowPersistenceRule())) {
                    FlowPersistenceRule flowPersistenceRule = JsonCommon.parse(persistenceTask.getFlowPersistenceRule(), FlowPersistenceRule.class);
                    url = url + "&" + flowPersistenceRule.convertToQueryString();
                }
                var outputStream = Files.newOutputStream(filePath, StandardOpenOption.WRITE);
                httpClient.download(outputStream,  url, null, Collections.emptyMap(), 60000);

                SCENE_TO_FILE_QUEUE_MAP.computeIfAbsent(persistenceTask.getScene(), k -> new PriorityQueue<Path>((a, b) -> a.toString().compareTo(b.toString())))
                        .offer(filePath);

            } else {
                throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("create file error"));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            persistenceTask.setLatestExecuteMessage(SysContextHolder.getCallChainId() + ":" + e.getMessage());
            arkimeFlowPersistenceTaskMapper.update(persistenceTask);
        } finally {
            if (Objects.nonNull(out)) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }





}
