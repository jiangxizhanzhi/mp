package cn.cyberpeace.mp.cscene.controller.collect.filemodify;

import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhParsedResult;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhQueryService;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import org.elasticsearch.client.RestHighLevelClient;

import java.time.LocalDateTime;
import java.util.List;

public class WazuhFileQueryService {

    private WazuhQueryService wazuhQueryService;

    public WazuhFileQueryService(RestHighLevelClient client) {
        this.wazuhQueryService = new WazuhQueryService(client);
    }

    public WazuhParsedResult<FileEventModel> queryFileModifyData(int from, int size, List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, List<Integer> levels, String filePath) {
        WazuhFileModifyDataQueryParamsBuilder paramsBuilder = new WazuhFileModifyDataQueryParamsBuilder(agentNames, startTime, endTime);
        paramsBuilder.setFilePath(filePath);
        paramsBuilder.setLevels(levels);
        WazuhFileModifyDataParser dataParser = new WazuhFileModifyDataParser();
        String[] indexArray = DataCollectionService.buildRequestIndex(startTime, endTime);
        WazuhParsedResult<FileEventModel> result = wazuhQueryService.queryParsedWazuhData(indexArray, from, size, paramsBuilder, dataParser);
        return result;
    }
}
