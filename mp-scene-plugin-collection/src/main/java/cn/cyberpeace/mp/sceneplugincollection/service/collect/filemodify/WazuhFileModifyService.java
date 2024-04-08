package cn.cyberpeace.mp.sceneplugincollection.service.collect.filemodify;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.WazuhDataQueryService;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.WazuhResult;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class WazuhFileModifyService {

    @Resource
    private WazuhDataQueryService wazuhDataQueryService;

    public WazuhResult<FileEventModel> queryFileModifyData(int from, int size, List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, List<Integer> levels, String filePath) {
        // 有采集插件 先拼接查询参数
        WazuhFileModifyDataQueryParamsBuilder paramsBuilder = new WazuhFileModifyDataQueryParamsBuilder(agentNames, startTime, endTime);
        paramsBuilder.setFilePath(filePath);
        paramsBuilder.setLevels(levels);
        BoolQueryBuilder queryBuilder = paramsBuilder.build();
        // 按时间倒序
        List<SortBuilder<?>> sortBuilders = new ArrayList<>();
        sortBuilders.add(SortBuilders.fieldSort("timestamp").order(SortOrder.DESC));
        // es索引
        String[] indexArray = DataCollectionService.buildRequestIndex(startTime, endTime);
        // 执行查询
        WazuhResult result = wazuhDataQueryService.queryWazuhData(indexArray, from, size, Arrays.asList(queryBuilder), sortBuilders);
        if (CollectionUtils.isEmpty(result.getList())) {
            return new WazuhResult<>(new ArrayList<>(), (int) result.getTotal());
        }
        var fileEventModels = this.getFileEventModels(result.getList());
        return new WazuhResult<>(fileEventModels, (int) result.getTotal());
    }

    private List<FileEventModel> getFileEventModels(List<WazuhFileRoot> wazuhFileRoots) {
        List<FileEventModel> fileEventModels = new ArrayList<>();
        for (WazuhFileRoot wazuhFileRoot : wazuhFileRoots) {
            FileEventModel fileEventModel = new FileEventModel();
            fileEventModel.setFileEventType(FileEventType.FILE);
            fileEventModel.setUpdateTime(TimeUtils.dateToLocalDateTime(wazuhFileRoot.getTimestamp()));
            fileEventModel.setAgentId(wazuhFileRoot.getAgent().getId());
            fileEventModel.setAgentName(wazuhFileRoot.getAgent().getName());
            if (wazuhFileRoot.getRule() != null) {
                fileEventModel.setLevel(wazuhFileRoot.getRule().getLevel());
            }
            if (wazuhFileRoot.getSyscheck() != null) {
                this.parsedOneSyscheckData(wazuhFileRoot, fileEventModel);
            } else {
                this.parsedOneScriptData(wazuhFileRoot, fileEventModel);
            }
            fileEventModels.add(fileEventModel);
        }
        return fileEventModels;
    }

    private void parsedOneScriptData(WazuhFileRoot wazuhFileRoot, FileEventModel fileEventModel) {
        WazuhFileRoot.CommonData commonData = wazuhFileRoot.getData();
        if (commonData == null) {
            return;
        }
        var launchOutput = commonData.getSicLaunchOutput();
        if (launchOutput == null) {
            return;
        }
        FileModifySicLaunchOutput output = JsonCommon.snakeMapToBean(launchOutput, FileModifySicLaunchOutput.class);
        fileEventModel.setFilePath(output.getData());
        if (Boolean.TRUE.equals(output.getExists())) {
            fileEventModel.setDescription("-");
        } else {
            String fileSize = output.getFileSize() == null ? "0" : output.getFileSize().toString();
            fileEventModel.setSize(fileSize);
            fileEventModel.setDescription("");
        }
    }

    private void parsedOneSyscheckData(WazuhFileRoot wazuhFileRoot, FileEventModel fileEventModel) {
        WazuhFileRoot.Syscheck syscheck = wazuhFileRoot.getSyscheck();
        if (syscheck != null) {
            fileEventModel.setContent(processContent(wazuhFileRoot));
            fileEventModel.setFilePath(syscheck.getPath());
            fileEventModel.setSize(syscheck.getSizeAfter());
            fileEventModel.setUser(syscheck.getUnameAfter());
            fileEventModel.setAction(syscheck.getEvent());
            fileEventModel.setMd5Before(syscheck.getMd5Before());
            fileEventModel.setMd5After(syscheck.getMd5After());
        }

        WazuhFileRoot.CommonData data = wazuhFileRoot.getData();
        if (data != null) {
            if (data.getWin() != null && data.getWin().getSystem() != null) {
                var system = data.getWin().getSystem();
                fileEventModel.setMessage(system.getMessage());
            }
            if (data.getSca() != null) {
                var sca = data.getSca();
                if (sca.getCheck() != null) {
                    fileEventModel.setMessage(sca.getCheck().getDescription());
                } else {
                    fileEventModel.setMessage(sca.getDescription());
                }
            }
        }

        WazuhFileRoot.Rule rule = wazuhFileRoot.getRule();
        if (rule != null) {
            fileEventModel.setDescription(rule.getDescription());
        }
    }

    private JSONObject processContent(WazuhFileRoot wazuhFileRoot) {
        JSONObject jsonObject = new JSONObject();
        WazuhFileRoot.Rule rule = wazuhFileRoot.getRule();
        if (Objects.nonNull(rule)) {
            JSONObject ruleObj = new JSONObject();
            ruleObj.put("firedtimes", rule.getFiredtimes());
            ruleObj.put("description", rule.getDescription());
            ruleObj.put("groups", rule.getGroups());
            ruleObj.put("id", rule.getId());
            jsonObject.put("rule", ruleObj);
        }
        jsonObject.put("full_log", wazuhFileRoot.getFullLog());
        return jsonObject;
    }

    @Getter
    @Setter
    private static class FileModifySicLaunchOutput {
        private Boolean isDir;
        private Long fileSize;
        private String data;
        private Boolean exists;
    }
}
