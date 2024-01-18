package cn.cyberpeace.mp.cscene.core;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ProcessReport extends Thread {

    private String csceneId;

    private String sceneId;

    private List<SceneElementBO> nodes;

    private Query query;

    private Map<String, String> nodeProcess = new HashMap<>();

    private CsceneAppConfig appConfig ;

    @Getter
    @Setter
    private static class Query {
        private List<String> queryNodeNames;

        private List<String> queryProcessKeywords;
    }

    public ProcessReport(String csceneId, String sceneId) {
        this.sceneId = sceneId;
        this.csceneId = csceneId;
        this.appConfig = BeanCommon.getBean(CsceneAppConfig.class);
        try {
            String result = FileUtils.readFileToString(new File("query.json"), "utf8");
            query = JsonCommon.snakeParse(result, Query.class);
        } catch (Exception e) {
            log.error("cscene id is {} ", csceneId, ExceptionUtils.getMessage(e));
        }
        if (query == null) {
            query = new Query();
            query.setQueryNodeNames(new ArrayList<>() {{
                this.add("Win10");
                this.add("Kail");
            }});
            query.setQueryProcessKeywords(new ArrayList<>() {{
                this.add("goby");
                this.add("zenmap.exe");
            }});
        }
    }


    @Override
    public void run() {
        if (this.query == null) {
            return;
        }
        this.initNodes();
        if (this.nodes.isEmpty()) {
            return;
        }

        var service = BeanCommon.getBean(ScenePluginCollectionSceneService.class);

        while (true) {
            CsceneInstanceModel cscene = BeanCommon.getBean(CsceneInstanceMapper.class).get(this.csceneId);
            if (cscene == null || Boolean.TRUE.equals(cscene.getDeleted())) {
                log.info("stop query process , cscene {} deleted", csceneId);
                break;
            } else {
                this.doQuery(service, this.nodes);
            }
            try {
                Thread.sleep(appConfig.getBcSleep());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void doQuery(ScenePluginCollectionSceneService service, List<SceneElementBO> nodes) {
        List<String> startProcessNodeIds = new ArrayList<>();
        for (SceneElementBO node : nodes) {
            String nodeId = node.getId();
            ListPageInfoQueryParams params = new ListPageInfoQueryParams();
            params.setSceneIds(Arrays.asList(this.sceneId));
            params.setNodeIds(Arrays.asList(nodeId));
            params.setKeyword(query.queryProcessKeywords);
            params.setPage(1);
            params.setPerPage(100);
            var result = service.listPageProcessV2(params, node.getEid());
            if (result.asResult().getData().getTotal() > 0) {
                // 从进程不存在到打开进程 这时候需要通知一次
                if (!this.nodeProcess.containsKey(nodeId)) {
                    this.nodeProcess.put(nodeId, nodeId);
                    startProcessNodeIds.add(nodeId);
                }
            } else {
                this.nodeProcess.remove(nodeId);
            }
        }
        if (!startProcessNodeIds.isEmpty()) {
            log.info("report process , cscene id is {} , nodeId is {}", csceneId, startProcessNodeIds);
            Params params = new Params();
            params.setNodeIds(startProcessNodeIds);
            params.setSceneId(this.csceneId);
            try {
                String res = HttpUtils.sendPost(appConfig.getBcUrl(), JsonCommon.snakeStringify(params));
                log.info("cscene id is {} , res is {}", this.csceneId, res);
            } catch (Exception e) {
                log.error("cscene id is {} , exception is ", this.csceneId, ExceptionUtils.getMessage(e));
            }
        }
    }

    @Getter
    @Setter
    private static class Params {
        private List<String> nodeIds;
        private String sceneId;
    }


    private void initNodes() {
        if (nodes == null) {
            nodes = new ArrayList<>();
            SceneHandler sceneHandler = new SceneHandler(this.sceneId, null, null);
            if (sceneHandler.getElementMap() != null) {
                for (Map.Entry<String, SceneElementBO> entry : sceneHandler.getElementMap().entrySet()) {
                    if (query.queryNodeNames.contains(entry.getValue().getName())) {
                        nodes.add(entry.getValue());
                    }
                }
            }
        }
    }
}
