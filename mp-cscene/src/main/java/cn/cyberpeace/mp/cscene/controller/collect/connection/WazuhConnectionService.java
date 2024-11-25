package cn.cyberpeace.mp.cscene.controller.collect.connection;

import cn.cyberpeace.mp.cscene.controller.collect.OneRowDataWrapper;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhParsedResult;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhQueryService;
import cn.cyberpeace.mp.situationaware.model.WazuhNetworkConnectionModel;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WazuhConnectionService {

    private WazuhQueryService wazuhQueryService;

    public WazuhConnectionService(RestHighLevelClient client) {
        this.wazuhQueryService = new WazuhQueryService(client);
    }

    public List<WazuhNetworkConnectionModel> queryCurrentConnectionData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        WazuhParsedResult<OneRowDataWrapper<WazuhNetworkConnectionModel>> wazuhParsedResult = this.getWazuhUserData(agentNames, startTime, endTime);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getValues();
    }

    public List<WazuhNetworkConnectionModel> queryAddedConnectionData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        WazuhParsedResult<OneRowDataWrapper<WazuhNetworkConnectionModel>> wazuhParsedResult = this.getWazuhUserData(agentNames, startTime, endTime);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getAdded();
    }

    public List<WazuhNetworkConnectionModel> queryDeletedConnectionData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        WazuhParsedResult<OneRowDataWrapper<WazuhNetworkConnectionModel>> wazuhParsedResult = this.getWazuhUserData(agentNames, startTime, endTime);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getDeletedPrev();
    }

    public List<WazuhNetworkConnectionModel> queryModifiedConnectionData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        WazuhParsedResult<OneRowDataWrapper<WazuhNetworkConnectionModel>> wazuhParsedResult = this.getWazuhUserData(agentNames, startTime, endTime);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getModified();
    }

    private WazuhParsedResult<OneRowDataWrapper<WazuhNetworkConnectionModel>> getWazuhUserData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        WazuhConnectionDataQueryParamsBuilder paramsBuilder = new WazuhConnectionDataQueryParamsBuilder(agentNames, startTime, endTime);
        WazuhConnectionDataParser dataParser = new WazuhConnectionDataParser();
        String[] indexArray = DataCollectionService.buildRequestIndex(startTime, endTime);
        WazuhParsedResult<OneRowDataWrapper<WazuhNetworkConnectionModel>> result = wazuhQueryService.queryParsedWazuhData(indexArray, 0, 1, paramsBuilder, dataParser);
        return result;
    }
}
