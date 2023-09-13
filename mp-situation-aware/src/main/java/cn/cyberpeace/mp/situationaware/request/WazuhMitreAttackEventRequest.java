package cn.cyberpeace.mp.situationaware.request;

import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author zhanzhi
 */
@Slf4j
@Data
@Accessors(chain = true)
@ApiModel("wazuh捕获匹配的mitre attack事件查询")
public class WazuhMitreAttackEventRequest {

    public static String MUST_MITRE_ID_FIELD = "rule.mitre.id";
    public static String AGENT_NAME = "agent.name";
    public static String ALL_INDEX = "*";

    @ApiModelProperty("节点id，对应eid")
    private List<String> agentIdList;

    @ApiModelProperty("分页大小")
    private Integer size;

    @ApiModelProperty("搜索索引范围，如果为空，则搜索所有")
    private List<String> indexList;

    /**
     * 目前查询都是使用timestamp排序，上一次查询结果中返回的最后一条条目的时间，使用search_after查询
     */
    @ApiModelProperty("如果是searchAfter分页搜索需要传入上一次搜索结果最后条目的排序字段值")
    private String utcZonedDateTimeString;

    @ApiModelProperty("是否是searchAfter分页搜索")
    private Boolean searchAfterFlag;

    /**
     * 如果lastSortTime为空，说明是深度分页查询
     */
    @ApiModelProperty("分页开始数目")
    private Integer from;


    public static SearchRequest getEsSearchRequest(WazuhMitreAttackEventRequest wazuhMitreAttackEventRequest) {
        TermsQueryBuilder termsQuery = QueryBuilders.termsQuery(AGENT_NAME, wazuhMitreAttackEventRequest.getAgentIdList());
        ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(MUST_MITRE_ID_FIELD);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(termsQuery);
        boolQuery.must(existsQuery);
        List<String> indexList = wazuhMitreAttackEventRequest.getIndexList();
        if (CollectionUtils.isEmpty(indexList)) {
            indexList = Collections.singletonList(ALL_INDEX);
        }
        // 构建查询请求
        SearchRequest searchRequest = new SearchRequest(indexList.toArray(new String[0]));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.size(wazuhMitreAttackEventRequest.getSize());
        if (Boolean.TRUE.equals(wazuhMitreAttackEventRequest.searchAfterFlag)) {
            searchSourceBuilder.searchAfter(new Object[] {wazuhMitreAttackEventRequest.getUtcZonedDateTimeString()});
        } else {
            searchSourceBuilder.from(wazuhMitreAttackEventRequest.getFrom());
        }
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.sort(SituationalAwareConstant.ES_TIMESTAMP_FIELD, SortOrder.DESC);
        searchRequest.source(searchSourceBuilder);

        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return searchRequest;
    }



}
