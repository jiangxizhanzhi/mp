package cn.cyberpeace.mp.cscene.controller.collect.process;

import cn.cyberpeace.mp.cscene.controller.collect.OneRowDataWrapper;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhParsedResult;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhQueryService;
import cn.cyberpeace.mp.situationaware.model.WazuhProcessModel;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import org.elasticsearch.client.RestHighLevelClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WazuhProcessQueryService {

    private WazuhQueryService wazuhQueryService;

    public WazuhProcessQueryService(RestHighLevelClient restHighLevelClient) {
        this.wazuhQueryService = new WazuhQueryService(restHighLevelClient);
    }

    public List<WazuhProcessModel> queryCurrentProcessData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, List<Integer> levels) {
        WazuhParsedResult<OneRowDataWrapper<WazuhProcessModel>> wazuhParsedResult = this.queryProcessWazuhData(agentNames, startTime, endTime, levels);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getValues();
    }

    public List<WazuhProcessModel> queryAddedProcessData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, List<Integer> levels) {
        WazuhParsedResult<OneRowDataWrapper<WazuhProcessModel>> wazuhParsedResult = this.queryProcessWazuhData(agentNames, startTime, endTime, levels);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getAdded();
    }

    public List<WazuhProcessModel> queryDeletedProcessData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, List<Integer> levels) {
        WazuhParsedResult<OneRowDataWrapper<WazuhProcessModel>> wazuhParsedResult = this.queryProcessWazuhData(agentNames, startTime, endTime, levels);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getDeletedPrev();
    }

    public List<WazuhProcessModel> queryModifiedProcessData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, List<Integer> levels) {
        WazuhParsedResult<OneRowDataWrapper<WazuhProcessModel>> wazuhParsedResult = this.queryProcessWazuhData(agentNames, startTime, endTime, levels);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getModified();
    }

    private WazuhParsedResult<OneRowDataWrapper<WazuhProcessModel>> queryProcessWazuhData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, List<Integer> levels) {
        WazuhProcessDataQueryParamsBuilder paramsBuilder = new WazuhProcessDataQueryParamsBuilder(agentNames, startTime, endTime);
        paramsBuilder.setLevels(levels);
        WazuhProcessDataParser dataParser = new WazuhProcessDataParser();
        String[] indexArray = DataCollectionService.buildRequestIndex(startTime, endTime);
        return wazuhQueryService.queryParsedWazuhData(indexArray, 0, 1, paramsBuilder, dataParser);
    }

}
