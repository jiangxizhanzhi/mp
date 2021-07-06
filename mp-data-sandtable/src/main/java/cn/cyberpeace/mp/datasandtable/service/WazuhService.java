package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.IteratorUtils;
import cn.cyberpeace.mp.base.util.LocalDateTimeUtils;
import cn.cyberpeace.mp.datasandtable.dao.WazuhDao;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhEventRequest;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileRoot;
import cn.cyberpeace.mp.datasandtable.model.WazuhModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhMonitorRoot;
import cn.cyberpeace.mp.datasandtable.model.emum.FileEventType;
import com.alibaba.fastjson.JSONObject;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * wazuh对接
 */
@Service
@Slf4j
public class WazuhService {

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private WazuhDao wazuhDao;

    private static final String wazuhAlertIndexPrefix = "wazuh-alerts-4.x-";

    /**
     * 查询文件事件分页
     *
     * @param wazuhEventRequest
     * @return ListObjResult<FileEventModel>
     */
    public ListObjResult<FileEventModel> queryFileEvents(WazuhEventRequest wazuhEventRequest) {
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest);
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        //构造查询条件
        buildFileEventBoolQueryBuilder(wazuhEventRequest, boolQueryBuilder, this.queryAllAgentMacAddr());
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        //处理分页
        int total = processPageable(wazuhEventRequest, indexArray, boolQueryBuilder, sourceBuilder);

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<WazuhFileRoot> wazuhFileRootList = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), WazuhFileRoot.class)).collect(Collectors.toList());
            List<FileEventModel> fileEventModelList = wazuhFileRootList.stream().map(this::getFileEventModel).collect(Collectors.toList());
            return new ListObjResult<>(fileEventModelList, total);
        } catch (IOException e) {
            log.error("es 查询文件事件分页 {}", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e.toString());
        }
    }

    /**
     * 查询监控数据 查询最近五分钟数据，监控指标频率为 1min, 对五分钟数据按照agentId进行分组，再去最近的时间的数据
     *
     * @param wazuhEventRequest wazuhEventRequest
     */
    public ListObjResult<WazuhModel> queryMonitorEvents(WazuhEventRequest wazuhEventRequest) {
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhEventRequest);
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        MultiValueMap<String, String> multiValueMap = this.queryAllAgentMacAddr();
        buildMonitorEventsBuilder(wazuhEventRequest, boolQueryBuilder, multiValueMap);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            List<WazuhMonitorRoot> wazuhFileRootList = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), WazuhMonitorRoot.class)).collect(Collectors.toList());
            Map<String, Optional<WazuhMonitorRoot>> wazuhMonitorMap =
                wazuhFileRootList.stream()
                    .filter(wazuhMonitorRoot -> wazuhMonitorRoot.getFullLog().contains("MonitorMessage:"))
                    .sorted(Comparator.comparing(WazuhMonitorRoot::getTimestamp))
                    .collect(Collectors.groupingBy(root -> root.getAgent().getId(),
                        Collectors.maxBy(Comparator.comparing(WazuhMonitorRoot::getTimestamp))));

            wazuhFileRootList = wazuhMonitorMap.values().stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            var wazuhModelList = wazuhFileRootList.stream()
                .filter(model -> model.getFullLog().contains("MonitorMessage:")).map(model -> {
                    var monitorMessage = model.getFullLog().substring("MonitorMessage:".length());
                    var result = JSONObject.parseObject(monitorMessage, WazuhModel.class);
                    result.setAgentId(model.getAgent().getId());
                    result.setMacId(findMacIdByAgentId(model.getAgent().getId(), wazuhEventRequest.getMacIds(), multiValueMap));
                    return result;
                }).collect(Collectors.toList());

            //process page
            int offset = wazuhEventRequest.getPageNum() * wazuhEventRequest.getPageSize();
            if (offset < wazuhModelList.size()) {
                return new ListObjResult<>(wazuhModelList.subList(offset, offset + wazuhEventRequest.getPageSize()), wazuhModelList.size());
            }

            return new ListObjResult<>(wazuhModelList, wazuhModelList.size());
        } catch (IOException e) {
            log.error("es 查询监控事件分页失败 {}", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e.toString());
        }
    }

    /**
     * 根据agent查询
     * @param agentId
     * @param macIds
     */
    private String findMacIdByAgentId(String agentId, List<String> macIds, MultiValueMap<String, String> multiValueMap) {
        List<String> ids = multiValueMap.get(agentId);
        var currentMacId = Sets.intersection(Sets.newHashSet(macIds), Sets.newHashSet(ids));
        return currentMacId.iterator().next();
    }

    /**
     * 文件监控时间过滤
     *
     * @param wazuhEventRequest wazuhEventRequest
     * @param boolQueryBuilder boolQueryBuilder
     */
    private void buildMonitorEventsBuilder(WazuhEventRequest wazuhEventRequest, BoolQueryBuilder boolQueryBuilder, MultiValueMap<String, String> allAgentMacAddr) {
        //构造查询条件
        boolQueryBuilder.must(QueryBuilders.termQuery("rule.id", "900002"));
        buildAgentIdQuery(wazuhEventRequest, boolQueryBuilder, allAgentMacAddr);
        //构造事件条件
        var end = LocalDateTime.now();
        var start = end.minusMinutes(5L);
        boolQueryBuilder.must(QueryBuilders.rangeQuery("timestamp")
            .from(LocalDateTimeUtils.localDateTimeToString(start, LocalDateTimeUtils.esdateTimeFormatter))
            .to(LocalDateTimeUtils.localDateTimeToString(end, LocalDateTimeUtils.esdateTimeFormatter)));
    }

    /**
     * 查询所有mac地址对应的
     *
     * @return
     */
    public MultiValueMap<String, String> queryAllAgentMacAddr() {
        MultiValueMap<String, String> responseMultiMap = new LinkedMultiValueMap<>();
        var wazuhNetifaceRoot = wazuhDao.queryAllAgentMac();
        wazuhNetifaceRoot.getData().getAffectedItems().forEach(item -> {
            responseMultiMap.add(item.getAgentId(), item.getMac());
        });

        return responseMultiMap;
    }

    /**
     * 根据mac地质查询AgentIds
     *
     * @param macs
     * @return agentSet
     */
    public Set<String> queryAgentIdsByMac(List<String> macs, MultiValueMap<String, String> allAgentMacAddr) {
        Set<String> agentSet = Sets.newHashSet();
        allAgentMacAddr.forEach((key, macList) -> {
            if (macList.stream().anyMatch(macs::contains)) {
                agentSet.add(key);
            }
        });

        return agentSet;
    }

    /**
     * 文件转换
     *
     * @param wazuhFileRoot
     * @return
     */
    private FileEventModel getFileEventModel(WazuhFileRoot wazuhFileRoot) {
        return FileEventModel.builder()
            .filePath(wazuhFileRoot.getSyscheck().getPath())
            .size(wazuhFileRoot.getSyscheck().getSizeAfter())
            .updateTime(LocalDateTimeUtils.dateToLocalDateTime(wazuhFileRoot.getTimestamp()))
            .user(wazuhFileRoot.getSyscheck().getUnameAfter())
            .action(wazuhFileRoot.getSyscheck().getEvent())
            //区分文件还是注册表
            .fileEventType(wazuhFileRoot.getRule().getGroups().stream().anyMatch("syscheck_registry"::equals) ? FileEventType.REGISTRY : FileEventType.FILE)
            .build();
    }

    /**
     * 处理分页
     *
     * @param wazuhEventRequest
     * @param indexArray
     * @param boolQueryBuilder
     * @param sourceBuilder
     * @return
     */
    private int processPageable(WazuhEventRequest wazuhEventRequest, String[] indexArray,
        BoolQueryBuilder boolQueryBuilder, SearchSourceBuilder sourceBuilder) {
        int offset = (wazuhEventRequest.getPageNum() - 1) * (wazuhEventRequest.getPageSize());
        sourceBuilder.from(offset);
        sourceBuilder.size(wazuhEventRequest.getPageSize());
        int total = queryTotalCount(indexArray, boolQueryBuilder);
        if (10000 < total) {
            total = 10000;
        }
        return total;
    }

    /**
     * 构造请求的索引
     *
     * @param wazuhEventRequest wazuhFileEventRequest
     * @return String[]
     */
    private String[] buildRequestIndex(WazuhEventRequest wazuhEventRequest) {
        List<String> indexList = indexListBuilder(wazuhEventRequest);
        var indexArray = new String[indexList.size()];
        indexList.toArray(indexArray);
        return indexArray;
    }

    private void buildFileEventBoolQueryBuilder(WazuhEventRequest wazuhEventRequest,
        BoolQueryBuilder boolQueryBuilder, MultiValueMap<String, String> allAgentMacAddr) {
        //构造查询条件
        boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("rule.groups", "syscheck")));

        //构造agentId
        buildAgentIdQuery(wazuhEventRequest, boolQueryBuilder, allAgentMacAddr);

        //构造事件条件
        boolQueryBuilder.must(QueryBuilders.rangeQuery("timestamp")
            .from(wazuhEventRequest.getStartTime().toString()).to(wazuhEventRequest.getEndTime().toString()));
    }

    /**
     * 构造agentIdQuery
     *
     * @param wazuhEventRequest wazuhFileEventRequest
     * @param boolQueryBuilder boolQueryBuilder
     */
    private void buildAgentIdQuery(WazuhEventRequest wazuhEventRequest, BoolQueryBuilder boolQueryBuilder, MultiValueMap<String, String> allAgentMacAddr) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("agent.id", this.queryAgentIdsByMac(wazuhEventRequest.getMacIds(), allAgentMacAddr)));
    }

    /**
     * 查询count
     *
     * @param indexArray
     * @param boolQueryBuilder
     * @return count
     */
    private int queryTotalCount(String[] indexArray, BoolQueryBuilder boolQueryBuilder) {
        var countRequest = new CountRequest(indexArray);
        countRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        countRequest.query(boolQueryBuilder);
        var total = 0;
        try {
            CountResponse totalSize = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            total = (int) totalSize.getCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * 构造查询需要的索引
     *
     * @return List<IndexBoost>
     */
    @VisibleForTesting
    protected List<String> indexListBuilder(WazuhEventRequest wazuhEventRequest) {
        List<String> dateStringList = fetchFormatterTime(wazuhEventRequest.getStartTime(), wazuhEventRequest.getEndTime());
        return dateStringList.stream().map(dateString -> wazuhAlertIndexPrefix + dateString).collect(Collectors.toList());
    }

    /**
     * 获取比如2021-05-01 到2021-05-20.所有的日期，方便构建所有的时间字符串2021.05.01,2021.05.02,....
     *
     * @param startTime startTime
     * @param endTime endTime
     * @return List<String>
     */
    private List<String> fetchFormatterTime(LocalDateTime startTime, LocalDateTime endTime) {
        var formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        long betweenDay = ChronoUnit.DAYS.between(startTime, endTime);
        int days = (int) betweenDay;
        return IntStream.range(0, days + 1).boxed().map(startTime::plusDays).map((time) -> time.format(formatter))
            .collect(Collectors.toList());
    }
}
