package cn.cyberpeace.mp.sceneplugincollection.service.collect.registrymodify;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.TimeHistogramInfo;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.WazuhDataQueryService;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.WazuhResult;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.LongBounds;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class WazuhRegistryModifyService {

    @Resource
    private WazuhDataQueryService wazuhDataQueryService;

    public WazuhResult<FileEventModel> queryRegistryModifyData(int from, int size, List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, List<Integer> levels, String filePath) {
        // 有采集插件 先拼接查询参数
        WazuhRegistryModifyDataQueryParamsBuilder paramsBuilder = new WazuhRegistryModifyDataQueryParamsBuilder(true, agentNames, startTime, endTime);
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

    public List<TimeHistogramInfo> aggsDateHistogram(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, String interval, String minBounds, String maxBounds) {
        String aggregationName = "aggs_date_histogram";
        //构造索引
        String[] indexArray = DataCollectionService.buildRequestIndex(startTime, endTime);
        // 查询条件
        WazuhRegistryModifyDataQueryParamsBuilder paramsBuilder = new WazuhRegistryModifyDataQueryParamsBuilder(false, agentNames, startTime, endTime);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(paramsBuilder.build());
        // 统计条件
        DateHistogramAggregationBuilder aggrBuilder = AggregationBuilders.dateHistogram(aggregationName)
                .field(SituationalAwareConstant.ES_TIMESTAMP_FIELD).fixedInterval(new DateHistogramInterval(interval))
                .order(BucketOrder.key(false)).minDocCount(0).extendedBounds(new LongBounds(minBounds, maxBounds))
                .format("yyyy-MM-dd HH:mm").timeZone(ZoneId.systemDefault());
        sourceBuilder.aggregation(aggrBuilder);

        SearchRequest searchRequest = new SearchRequest(indexArray);
        searchRequest.source(sourceBuilder);
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        var aggregations = wazuhDataQueryService.aggregations(searchRequest);
        List<TimeHistogramInfo> infos = new ArrayList<>();
        if (Objects.nonNull(aggregations)) {
            Histogram histogram = aggregations.get(aggregationName);
            histogram.getBuckets().forEach(b -> {
                String time = b.getKeyAsString();
                long count = Long.valueOf(b.getDocCount());
                LocalDateTime localDateTime = TimeUtils.yyyyMMddHHmm2LocalDate(time);
                infos.add(new TimeHistogramInfo(time, count, localDateTime));
            });
        }
        Collections.sort(infos, Comparator.comparing(TimeHistogramInfo::getLocalDateTime));
        return infos;
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
        FileRegistrySicLaunchOutput output = JsonCommon.snakeMapToBean(launchOutput, FileRegistrySicLaunchOutput.class);
        fileEventModel.setFilePath(output.getRegPath());
        if (Boolean.TRUE.equals(output.getExists())) {
            Map<String, String> fullLog = new HashMap<>();
            fullLog.put(output.getRegPath(), "exist");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("full_log", fullLog);
            fileEventModel.setContent(jsonObject);
            fileEventModel.setDescription("");
        } else {
            Map<String, String> fullLog = new HashMap<>();
            fullLog.put(output.getRegPath(), "not exist");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("full_log", fullLog);
            fileEventModel.setContent(jsonObject);
            fileEventModel.setDescription("-");
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
    private static class FileRegistrySicLaunchOutput {
        private Boolean isDir;
        private String regPath;
        private Boolean exists;
    }
}
