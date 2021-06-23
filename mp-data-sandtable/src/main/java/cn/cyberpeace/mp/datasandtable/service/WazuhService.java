package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.LocalDateTimeUtils;
import cn.cyberpeace.mp.datasandtable.dao.WazuhDao;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileEventRequest;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileRoot;
import cn.cyberpeace.mp.datasandtable.model.WazuhNetifaceRoot;
import cn.cyberpeace.mp.datasandtable.model.emum.FileEventType;
import com.alibaba.fastjson.JSONObject;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
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
     * @param wazuhFileEventRequest
     * @return ListObjResult<FileEventModel>
     */
    public ListObjResult<FileEventModel> queryFileEvents(WazuhFileEventRequest wazuhFileEventRequest){
        //根据日期构造索引
        String[] indexArray = buildRequestIndex(wazuhFileEventRequest);
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();

        //构造查询条件
        buildBoolQueryBuilder(wazuhFileEventRequest, boolQueryBuilder);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        //处理分页
        int total = processPageable(wazuhFileEventRequest, indexArray, boolQueryBuilder, sourceBuilder);

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
     * 查询所有mac地址对应的
     * @return
     */
    public MultiValueMap<String, String> queryAllAgentMacAddr() {
        MultiValueMap<String, String> responseMultiMap = new LinkedMultiValueMap<>();
        var wazuhNetifaceRoot = wazuhDao.queryAllAgentMac();
        wazuhNetifaceRoot.getData().getAffectedItems().forEach(item->{
            responseMultiMap.add(item.getAgentId(), item.getMac());
        });

        return responseMultiMap;
    }


    /**
     * 根据mac地质查询AgentIds
     * @param macs
     * @return agentSet
     */
    public Set<String> queryAgentIdsByMac(List<String> macs) {
        MultiValueMap<String, String> multiValueMap = this.queryAllAgentMacAddr();
        Set<String> agentSet = Sets.newHashSet();
        multiValueMap.forEach((key, macList) -> {
            if (macList.stream().anyMatch(macs::contains)) {
                agentSet.add(key);
            }
        });

        return agentSet;
    }

    /**
     * 文件转换
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
     * @param wazuhFileEventRequest
     * @param indexArray
     * @param boolQueryBuilder
     * @param sourceBuilder
     * @return
     */
    private int processPageable(WazuhFileEventRequest wazuhFileEventRequest, String[] indexArray,
        BoolQueryBuilder boolQueryBuilder, SearchSourceBuilder sourceBuilder) {
        int  offset = (wazuhFileEventRequest.getPageNum() -1) * (wazuhFileEventRequest.getPageSize());
        sourceBuilder.from(offset);
        sourceBuilder.size(wazuhFileEventRequest.getPageSize());
        int total = queryTotalCount(indexArray, boolQueryBuilder);
        return total;
    }

    /**
     * 构造请求的索引
     * @param wazuhFileEventRequest wazuhFileEventRequest
     * @return String[]
     */
    private String[] buildRequestIndex(WazuhFileEventRequest wazuhFileEventRequest) {
        List<String> indexList = indexListBuilder(wazuhFileEventRequest);
        var indexArray = new String[indexList.size()];
        indexList.toArray(indexArray);
        return indexArray;
    }

    private void buildBoolQueryBuilder(WazuhFileEventRequest wazuhFileEventRequest, BoolQueryBuilder boolQueryBuilder) {
        //构造查询条件
        boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("rule.groups", "syscheck")));

        //构造agentId
        buildAgentIdQuery(wazuhFileEventRequest, boolQueryBuilder);

        //构造事件条件
        boolQueryBuilder.must(QueryBuilders.rangeQuery("timestamp")
            .from(wazuhFileEventRequest.getStartTime().toString()).to(wazuhFileEventRequest.getEndTime().toString()));
    }

    /**
     * 构造agentIdQuery
     * @param wazuhFileEventRequest wazuhFileEventRequest
     * @param boolQueryBuilder boolQueryBuilder
     */
    private void buildAgentIdQuery(WazuhFileEventRequest wazuhFileEventRequest, BoolQueryBuilder boolQueryBuilder) {
        var agentIdBoolQuery = QueryBuilders.boolQuery();
        this.queryAgentIdsByMac(wazuhFileEventRequest.getMacIds()).forEach(agentId -> {
            agentIdBoolQuery.must(QueryBuilders.termQuery("agent.id", agentId));
        });
        boolQueryBuilder.should(agentIdBoolQuery);
    }

    /**
     * 查询count
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
    protected List<String> indexListBuilder(WazuhFileEventRequest wazuhFileEventRequest) {
        List<String> dateStringList = fetchFormatterTime(wazuhFileEventRequest.getStartTime(), wazuhFileEventRequest.getEndTime());
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
