package cn.cyberpeace.mp.cscene.controller.collect.user;

import cn.cyberpeace.mp.cscene.controller.collect.OneRowDataWrapper;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhParsedResult;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhQueryService;
import cn.cyberpeace.mp.situationaware.model.WazuhSystemUserModel;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import org.elasticsearch.client.RestHighLevelClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WazuhSystemUserService {

    private WazuhQueryService wazuhQueryService;

    public WazuhSystemUserService(RestHighLevelClient restHighLevelClient) {
        this.wazuhQueryService = new WazuhQueryService(restHighLevelClient);
    }

    public List<WazuhSystemUserModel> queryCurrentSystemUserData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        WazuhParsedResult<OneRowDataWrapper<WazuhSystemUserModel>> wazuhParsedResult = this.getWazuhUserData(agentNames, startTime, endTime);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getValues();
    }

    public List<WazuhSystemUserModel> queryAddedSystemUserData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        WazuhParsedResult<OneRowDataWrapper<WazuhSystemUserModel>> wazuhParsedResult = this.getWazuhUserData(agentNames, startTime, endTime);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getAdded();
    }

    public List<WazuhSystemUserModel> queryDeletedSystemUserData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        WazuhParsedResult<OneRowDataWrapper<WazuhSystemUserModel>> wazuhParsedResult = this.getWazuhUserData(agentNames, startTime, endTime);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getDeletedPrev();
    }

    public List<WazuhSystemUserModel> queryModifiedSystemUserData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        WazuhParsedResult<OneRowDataWrapper<WazuhSystemUserModel>> wazuhParsedResult = this.getWazuhUserData(agentNames, startTime, endTime);
        if (wazuhParsedResult.getData() == null || wazuhParsedResult.getData().isEmpty()) {
            return new ArrayList<>();
        }
        return wazuhParsedResult.getData().get(0).getModified();
    }

    private WazuhParsedResult<OneRowDataWrapper<WazuhSystemUserModel>> getWazuhUserData(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime) {
        WazuhSystemUserDataQueryParamsBuilder paramsBuilder = new WazuhSystemUserDataQueryParamsBuilder(agentNames, startTime, endTime);
        WazuhSystemUserDataParser dataParser = new WazuhSystemUserDataParser();
        String[] indexArray = DataCollectionService.buildRequestIndex(startTime, endTime);
        WazuhParsedResult<OneRowDataWrapper<WazuhSystemUserModel>> result = wazuhQueryService.queryParsedWazuhData(indexArray, 0, 1, paramsBuilder, dataParser);
        return result;
    }
}
