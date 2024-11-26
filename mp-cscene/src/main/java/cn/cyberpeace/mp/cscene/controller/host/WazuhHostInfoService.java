package cn.cyberpeace.mp.cscene.controller.host;

import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhParsedResult;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhQueryService;
import cn.cyberpeace.mp.situationaware.model.WazuhHostStateModel;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import org.elasticsearch.client.RestHighLevelClient;

import java.time.LocalDateTime;
import java.util.List;

public class WazuhHostInfoService {

    private WazuhQueryService wazuhQueryService;

    public WazuhHostInfoService(RestHighLevelClient client) {
        this.wazuhQueryService = new WazuhQueryService(client);
    }

    public WazuhParsedResult<WazuhHostStateModel> getWazuhHostInfoData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime,int from ,int size) {
        WazuhHostInfoQueryParamsBuilder paramsBuilder = new WazuhHostInfoQueryParamsBuilder(agentNames, startTime, endTime);
        WazuhHostInfoDataParser dataParser = new WazuhHostInfoDataParser();
        String[] indexArray = DataCollectionService.buildRequestIndex(startTime, endTime);
        WazuhParsedResult<WazuhHostStateModel> result = wazuhQueryService.queryParsedWazuhData(indexArray, from, size, paramsBuilder, dataParser);
        return result;
    }
}
