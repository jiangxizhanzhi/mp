package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.LocalDateTimeUtils;
import cn.cyberpeace.mp.datasandtable.dao.WazuhDao;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileEventRequest;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileRoot;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * wazuh对接
 */
@Service
public class WazuhService {

    @Autowired
    private WazuhDao wazuhDao;

    /**
     * 查询文件事件分页
     * @param wazuhFileEventRequest
     * @return ListObjResult<FileEventModel>
     */
    public ListObjResult<FileEventModel> queryFileEvents(WazuhFileEventRequest wazuhFileEventRequest){
        String requestUri = "/syscheck" + wazuhFileEventRequest.getAgentId() + "?";
        requestUri = buildFileRequest(wazuhFileEventRequest, requestUri);
        WazuhFileRoot wazuhFileRoot = wazuhDao.queryFileEvents(requestUri);
        List<FileEventModel> fileEventModels = wazuhFileRoot.getData().getAffectedItems().stream().map(fileItem ->
            FileEventModel.builder().
                build()).collect(Collectors.toList());
        return ListObjResult.<FileEventModel>builder().total(wazuhFileRoot.getData().getTotalAffectedItems()).
            objs(fileEventModels).build();
    }

    /**
     * 构造wazuh请求参数
     * @param wazuhFileEventRequest wazuhFileEventRequest
     * @param requestUri requestUri
     * @return String
     */
    private String buildFileRequest(WazuhFileEventRequest wazuhFileEventRequest, String requestUri) {
        StringBuilder resUri = new StringBuilder(requestUri);
        //类型
        resUri.append("type=").append(wazuhFileEventRequest.getType())
            //分页参数
            .append("&limit=").append(wazuhFileEventRequest.getPageSize()).append("&offset=").append(wazuhFileEventRequest.getPageNum() * wazuhFileEventRequest.getPageSize())
            //构造时间范围查询
            .append("q=").append("date<").append(LocalDateTimeUtils.localDateTimeToString(wazuhFileEventRequest.getEndTime(), LocalDateTimeUtils.dateTimeFormatter1))
            .append("date>").append(LocalDateTimeUtils.localDateTimeToString(wazuhFileEventRequest.getStartTime(), LocalDateTimeUtils.dateTimeFormatter1))

        ;

        return resUri.toString();
    }
}
