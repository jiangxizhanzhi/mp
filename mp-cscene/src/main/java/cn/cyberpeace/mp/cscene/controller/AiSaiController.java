package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.controller.collect.connection.WazuhConnectionService;
import cn.cyberpeace.mp.cscene.controller.collect.filemodify.WazuhFileQueryService;
import cn.cyberpeace.mp.cscene.controller.collect.host.WazuhHostInfoService;
import cn.cyberpeace.mp.cscene.controller.collect.process.WazuhProcessQueryService;
import cn.cyberpeace.mp.cscene.controller.collect.user.WazuhSystemUserService;
import cn.cyberpeace.mp.sceneplugincollection.bo.AttackInfo;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionFlowBO;
import cn.cyberpeace.mp.situationaware.bo.FlowLogBO;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhHostStateModel;
import cn.cyberpeace.mp.situationaware.model.WazuhNetworkConnectionModel;
import cn.cyberpeace.mp.situationaware.model.WazuhProcessModel;
import cn.cyberpeace.mp.situationaware.model.WazuhProcessModelV2;
import cn.cyberpeace.mp.situationaware.model.WazuhSystemUserModel;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.utils.FileUtils;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.Api;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
public class AiSaiController {

    private Map<String, TeamInfo> teamInfoMap;

    public static RestHighLevelClient createClient(String ip, Integer port) {
        // 通过 IP 和端口构建 RestHighLevelClient
        // 你可以指定多个 HttpHost，用于连接到多个 Elasticsearch 节点
        return new RestHighLevelClient(RestClient.builder(new HttpHost(ip, port, "http")  // 第一台节点
        ));
    }


    public void init() {
        if (this.teamInfoMap != null) {
            return;
        }
        teamInfoMap = new HashMap<>();
        List<TeamInfo> teamInfoList = null;
        File file = new File("/home/team.json");
        if (file.exists()) {
            String content = FileUtil.readString(file, "utf8");
            teamInfoList = JsonCommon.snakeParse(content, new TypeReference<List<TeamInfo>>() {
            });
        } else {
            Resource[] resources = new Resource[0];
            try {
                resources = new PathMatchingResourcePatternResolver().getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "team.json");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String content = null;
            try {
                content = FileUtils.readLine(resources);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            teamInfoList = JsonCommon.snakeParse(content, new TypeReference<List<TeamInfo>>() {
            });
        }

        for (var t : teamInfoList) {
            var es = createClient(t.getEsIp(), t.getEsPort());
            t.setRestClient(es);
            t.setAgentNameIp(new HashMap<>());
            for (Map.Entry<String, String> entry : t.getIpAgentName().entrySet()) {
                t.getAgentNameIp().put(entry.getValue(), entry.getKey());
            }
            this.teamInfoMap.put(t.getTeamId(), t);
        }
    }

    @GetMapping("/flow/page")
    public ListObjServiceResult<CollectionFlowBO.Output> pageFlow(FlowQueryParams queryParams) {
        this.init();
        // 创建查询请求
        SearchRequest searchRequest = new SearchRequest("mp*");

        // 构建查询
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        var boolquery = QueryBuilders.boolQuery();
        boolquery.must(QueryBuilders.termsQuery("source.ip", queryParams.getAgentIps()));
//                boolquery.must(QueryBuilders.termsQuery("destination.ip", queryParams.getAgentIps()));
//        boolquery.must(QueryBuilders.existsQuery("suricata"));
        if (queryParams.getProtocol() != null) {
            boolquery.must(QueryBuilders.termQuery("protocol", queryParams.getProtocol()));
        }
        if (queryParams.getPort() != null) {
            boolquery.must(QueryBuilders.boolQuery()
                                   .should(QueryBuilders.termQuery("source.port", queryParams.getPort()))
                                   .should(QueryBuilders.termQuery("destination.port", queryParams.getPort())));
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

        List<CollectionFlowBO> bos = new ArrayList<>();
        List<CollectionFlowBO.Output> ll = new ArrayList<>();
        hits.forEach(hit -> {
            FlowLogBO flowLogBO = JSONObject.parseObject(hit.getSourceAsString(), FlowLogBO.class);
            CollectionFlowBO flowBO = new CollectionFlowBO();
            if (flowLogBO.getTimestamp() != null) {
                flowBO.setTime(TimeUtils.timestampToLocalDateTime(flowLogBO.getTimestamp()));
                flowBO.setTimestamp(flowLogBO.getTimestamp());
            }
            if (flowLogBO.getSource() != null) {
                String ip = flowLogBO.getSource().getIp();
                flowBO.setSourceIp(ip);
                flowBO.setSourcePort(String.valueOf(flowLogBO.getSource().getPort()));
                flowBO.setSourceMac(flowLogBO.getSource().getMac());
            }
            if (flowLogBO.getDestination() != null) {
                String ip = flowLogBO.getDestination().getIp();
                flowBO.setTargetPort(String.valueOf(flowLogBO.getDestination().getPort()));
                flowBO.setTargetMac(flowLogBO.getDestination().getMac());
                flowBO.setTargetIp(ip);
            }
            if (flowLogBO.getSuricata() != null) {
                List<String> category = flowLogBO.getSuricata().getCategory();
                List<Integer> severity = flowLogBO.getSuricata().getSeverity();
                if (CollectionUtils.isNotEmpty(category) && CollectionUtils.isNotEmpty(severity)) {
                    int attackInfoNum = category.size() < severity.size() ? category.size() : severity.size();
                    List<AttackInfo> attackInfos = new ArrayList<>();
                    flowBO.setAttackInfos(attackInfos);
                    for (int i = 0; i < attackInfoNum; i++) {
                        attackInfos.add(new AttackInfo(severity.get(i), category.get(i)));
                    }
                }
            }
            if (flowLogBO.getNetwork() != null) {
                flowBO.setSize(flowLogBO.getNetwork().getBytes());
            }
            flowBO.setProtocol(flowLogBO.getProtocol());
            ll.add(flowBO.toOutput());
            bos.add(flowBO);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(ll, total)).asResult();
    }

    @GetMapping("/wazuh/process_info")
    public ListObjServiceResult<WazuhProcessModelV2> pageWazuh(WazuhQueryParams queryParams) {
        this.init();
        var team = teamInfoMap.get(queryParams.getTeamId());
        var restClient = team.getRestClient();
        List<String> agentNames = new ArrayList<>();
        var map = teamInfoMap.get(queryParams.getTeamId()).getIpAgentName();
        for (String agentIp : queryParams.getAgentIps()) {
            if (map.containsKey(agentIp)) {
                agentNames.add(map.get(agentIp));
            }
        }

        int size = queryParams.getPerPage();
        int from = (queryParams.getPage() - 1) * size;
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(10);

        var res = new WazuhProcessQueryService(restClient).queryCurrentProcessData(agentNames, start, end, null);
        var result = res.subList(from, from + size >= res.size() ? res.size() : from + size);

        for (WazuhProcessModel r : result) {
            r.setAgentIp(team.getAgentNameIp().get(r.getAgentName()));
        }

        var ll = new ArrayList<WazuhProcessModelV2>();
        for (WazuhProcessModel r : result) {
            WazuhProcessModelV2 v2 = new WazuhProcessModelV2();
            BeanUtils.copyProperties(r, v2);

            DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(v2.getCreateTime(), inputFormatter);
            LocalDateTime localDateTime = offsetDateTime.atZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                    .toLocalDateTime();
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String output = localDateTime.format(outputFormatter);
            v2.setTime(output);

            ll.add(v2);
        }

        return new ListObjServiceResult<>(new ListObjResult<>(ll, res.size())).asResult();
    }

    @GetMapping("/wazuh/system_user")
    public ListObjServiceResult<WazuhSystemUserModel.Output> pageFile(WazuhQueryParams queryParams) {
        this.init();
        var team = teamInfoMap.get(queryParams.getTeamId());
        var restClient = team.getRestClient();
        List<String> agentNames = new ArrayList<>();
        var map = teamInfoMap.get(queryParams.getTeamId()).getIpAgentName();
        for (String agentIp : queryParams.getAgentIps()) {
            if (map.containsKey(agentIp)) {
                agentNames.add(map.get(agentIp));
            }
        }

        int size = queryParams.getPerPage();
        int from = (queryParams.getPage() - 1) * size;
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(10);
        var res = new WazuhSystemUserService(restClient).queryCurrentSystemUserData(agentNames, start, end);
        var result = res.subList(from, from + size >= res.size() ? res.size() : from + size);
        List<WazuhSystemUserModel.Output> ll = new ArrayList<>();
        for (WazuhSystemUserModel r : result) {
            r.setAgentIp(team.getAgentNameIp().get(r.getAgentName()));
            ll.add(r.toOutput());
        }
        return new ListObjServiceResult<>(new ListObjResult<>(ll, res.size())).asResult();
    }

    @GetMapping("/wazuh/file_events")
    public ListObjServiceResult<FileEventModel> pageUser(WazuhQueryParams queryParams) {
        this.init();
        var team = teamInfoMap.get(queryParams.getTeamId());
        var restClient = team.getRestClient();

        List<String> agentNames = new ArrayList<>();
        var map = teamInfoMap.get(queryParams.getTeamId()).getIpAgentName();
        for (String agentIp : queryParams.getAgentIps()) {
            if (map.containsKey(agentIp)) {
                agentNames.add(map.get(agentIp));
            }
        }

        int size = queryParams.getPerPage();
        int from = (queryParams.getPage() - 1) * size;
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusMinutes(200);
        var res = new WazuhFileQueryService(restClient).queryFileModifyData(from, size, agentNames, start, end, null, null);
        for (FileEventModel r : res.getData()) {
            r.setAgentIp(team.getAgentNameIp().get(r.getAgentName()));
        }
        return new ListObjServiceResult<>(new ListObjResult<>(res.getData(), (int) res.getTotal())).asResult();
    }

    @GetMapping("/wazuh/port_info")
    public ListObjServiceResult<WazuhNetworkConnectionModel.Output> pagePorts(WazuhQueryParams queryParams) {
        this.init();
        var team = teamInfoMap.get(queryParams.getTeamId());
        var restClient = team.getRestClient();

        List<String> agentNames = new ArrayList<>();
        var map = teamInfoMap.get(queryParams.getTeamId()).getIpAgentName();
        for (String agentIp : queryParams.getAgentIps()) {
            if (map.containsKey(agentIp)) {
                agentNames.add(map.get(agentIp));
            }
        }

        int size = queryParams.getPerPage();
        int from = (queryParams.getPage() - 1) * size;
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(10);

        var res = new WazuhConnectionService(restClient).queryCurrentConnectionData(agentNames, start, end);
        var result = res.subList(from, from + size >= res.size() ? res.size() : from + size);

        List<WazuhNetworkConnectionModel.Output> ll = new ArrayList<>();
        for (WazuhNetworkConnectionModel r : result) {
            r.setAgentIp(team.getAgentNameIp().get(r.getAgentName()));
            ll.add(r.toOutput());
        }
        return new ListObjServiceResult<>(new ListObjResult<>(ll, res.size())).asResult();
    }

    @GetMapping("/wazuh/host_info")
    public ListObjServiceResult<WazuhHostStateModel.Output> hostInfo(WazuhQueryParams queryParams) {
        this.init();
        var team = teamInfoMap.get(queryParams.getTeamId());
        var rest = team.getRestClient();

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(10);
        String[] indexArray = DataCollectionService.buildRequestIndex(start, end);

        List<String> agentNames = new ArrayList<>();
        var map = teamInfoMap.get(queryParams.getTeamId()).getIpAgentName();
        for (String agentIp : queryParams.getAgentIps()) {
            if (map.containsKey(agentIp)) {
                agentNames.add(map.get(agentIp));
            }
        }

        int size = queryParams.getPerPage();
        int from = (queryParams.getPage() - 1) * size;
        var res = new WazuhHostInfoService(rest).getWazuhHostInfoData(agentNames, start, end, from, size);
        List<WazuhHostStateModel.Output> ll = new ArrayList<>();
        for (WazuhHostStateModel r : res.getData()) {
            r.setAgentIp(team.getAgentNameIp().get(r.getAgentName()));
            ll.add(r.toOuput());
        }


        return new ListObjServiceResult<>(new ListObjResult<>(ll, (int) res.getTotal())).asResult();
    }

    @Getter
    @Setter
    public static class TeamInfo {
        private String teamId;
        private String teamName;
        private List<String> agentIps;
        private Map<String, String> ipAgentName;
        private Map<String, String> agentNameIp;
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

    @Getter
    @Setter
    public static class WazuhQueryParams {
        private int perPage = 10;
        private int page = 1;
        private String teamId;
        private String[] agentIps;
        int host;
    }


}
