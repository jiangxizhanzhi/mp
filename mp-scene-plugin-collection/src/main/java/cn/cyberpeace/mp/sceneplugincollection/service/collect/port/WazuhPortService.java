package cn.cyberpeace.mp.sceneplugincollection.service.collect.port;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.WazuhDataQueryService;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.WazuhResult;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

@Slf4j
@Service
public class WazuhPortService {

    @Resource
    private WazuhDataQueryService wazuhDataQueryService;

    public WazuhResult<WazuhPortModel> queryPortData(int from, int size, List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        // 有采集插件 先拼接查询参数
        WazuhPortDataQueryParamsBuilder paramsBuilder = new WazuhPortDataQueryParamsBuilder(agentNames, startTime, endTime);
        BoolQueryBuilder queryBuilder = paramsBuilder.build();
        // 按时间倒序
        List<SortBuilder<?>> sortBuilders = new ArrayList<>();
        sortBuilders.add(SortBuilders.fieldSort("timestamp").order(SortOrder.DESC));
        // es索引
        String[] indexArray = DataCollectionService.buildRequestIndex(startTime, endTime);
        // 执行查询
        WazuhResult<WazuhFileRoot> result = wazuhDataQueryService.queryWazuhData(indexArray, 0, 5, Arrays.asList(queryBuilder), sortBuilders);
        if (CollectionUtils.isEmpty(result.getList())) {
            return new WazuhResult<>(new ArrayList<>(), 0);
        }
        var fileEventModels = this.parsePortModels(result.getList());

        // 计算实际起始位置
        int start = Math.min(from, fileEventModels.getList().size());
        // 计算结束位置（不超过列表大小）
        int end = Math.min(start + size, fileEventModels.getList().size());
        // 处理起始位置超过列表大小的情况
        if (start >= fileEventModels.getList().size()) {
            return new WazuhResult<>(new ArrayList<>(), (int) result.getTotal());
        }

        List<WazuhPortModel> subList = fileEventModels.getList().subList(start, end);
        return new WazuhResult<>(subList, fileEventModels.getTotal());
    }

    private WazuhResult<WazuhPortModel> parsePortModels(List<WazuhFileRoot> wazuhFileRoots) {
        WazuhResult<WazuhPortModel> result = new WazuhResult<>();
        result.setList(new ArrayList<>());
        result.setTotal(0);

        for (var inputData : wazuhFileRoots) {
            if (inputData.getData() == null || inputData.getData().getSicLaunchOutput() == null) {
                continue;
            }
            if (inputData.getData().getSicLaunchOutput().get("data") == null) {
                continue;
            }
            String data = inputData.getData().getSicLaunchOutput().get("data").toString();
            data = StringUtils.strip(data, "\"");
            data = TextUtils.decompress(TextUtils.rawDecodeBase64(data));
            try {
                WazuhPortModelWrapper wrapper = JsonCommon.snakeParse(data, WazuhPortModelWrapper.class);
                if (wrapper.getValues() != null) {
                    result.setTotal(wrapper.getValues().size());
                    result.getList().addAll(wrapper.getValues());
                    for (var v : result.getList()) {
                        v.setAgentName(inputData.getAgent().getName());
                        v.setAgentId(inputData.getAgent().getId());
                    }
                }
                break;
            } catch (Exception e) {
                log.error("failed to parse port data , {}", data, e);
            }
        }
        return result;
    }
}
