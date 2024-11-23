package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackDto;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.Api;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 11:22
 */
@Api(value = "主机状态监控", tags = "主机状态监控")
@Slf4j
@RestController
@RequestMapping()
public class AiSaiController {

    private Map<String, TeamInfo> teamInfoMap = new HashMap<>();

    public static RestHighLevelClient createClient(String ip, Integer port) {
        // 通过 IP 和端口构建 RestHighLevelClient
        // 你可以指定多个 HttpHost，用于连接到多个 Elasticsearch 节点
        return new RestHighLevelClient(RestClient.builder(new HttpHost(ip, port, "http")  // 第一台节点
        ));
    }

    @PostConstruct
    public void init() {
        var content = FileUtil.readString("teams.jsom", "utf-8");
        List<TeamInfo> teamInfoList = JsonCommon.parse(content, new TypeReference<List<TeamInfo>>() {
        });
        for (var t : teamInfoList) {
            var es = createClient(t.getEsIp(), t.getEsPort());
            t.setRestClient(es);
        }
    }

    @GetMapping("/flow/page")
    public ListObjServiceResult<FlowAttackDto> pageFlow(@RequestParam FlowQueryParams queryParams) {
        // 创建查询请求
        SearchRequest searchRequest = new SearchRequest("mp_arkime*");

        // 构建查询
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        var boolquery = QueryBuilders.boolQuery();
        boolquery.must(QueryBuilders.termsQuery("source.ip", queryParams.getAgentIps())).must(QueryBuilders.termsQuery("destination.ip", queryParams.getAgentIps()));
        if (queryParams.getProtocol() != null) {
            boolquery.must(QueryBuilders.termQuery("protocol", queryParams.getProtocol()));
        }
        if (queryParams.getPort() != null) {
            boolquery.must(QueryBuilders.boolQuery().should(QueryBuilders.termQuery("source.port", queryParams.getPort())).should(QueryBuilders.termQuery("destination.port", queryParams.getPort())));
        }
        if (StringUtils.isNotEmpty(queryParams.getStartTime()) && StringUtils.isNotEmpty(queryParams.getEndTime())) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
            var start = TimeUtils.stringToLocalDateTime(queryParams.getStartTime());
            var end = TimeUtils.stringToLocalDateTime(queryParams.getEndTime());
            rangeQueryBuilder.gte(start);
            rangeQueryBuilder.lte(end);
            boolquery.must(rangeQueryBuilder);
        }
        sourceBuilder.query(boolquery);
        sourceBuilder.sort("@timestamp", SortOrder.DESC);
        sourceBuilder.trackTotalHits(true);

        int size = queryParams.getPerPage();
        int from = (queryParams.getPage() - 1) * size;
        sourceBuilder.size(size);
        sourceBuilder.from(from);

        searchRequest.source(sourceBuilder);

        var restClient = teamInfoMap.get(queryParams.getTeamId()).getRestClient();
        // 执行查询
        SearchResponse searchResponse = null;
        try {
            searchResponse = restClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 获取查询结果
        SearchHits hits = searchResponse.getHits();
        int total = (int) hits.getTotalHits().value;


        hits.forEach(hit -> {
            System.out.println("Document: " + hit.getSourceAsString());
        });
        return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), total));
    }

    @Getter
    @Setter
    public static class TeamInfo {
        private String teamId;
        private String teamName;
        private String agentIps;
        private String esIp;
        private Integer esPort;
        private RestHighLevelClient restClient;
    }


    @Getter
    @Setter
    public static class FlowQueryParams {
        private int perPage = 10;
        private int page = 1;
        private String teamId;
        private String[] agentIps;
        private String protocol;
        private String startTime;
        private String endTime;
        private Integer port;
    }


}
