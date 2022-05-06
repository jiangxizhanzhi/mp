package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.cscene.dto.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/6 15:29
 */
@Service
public class LogFileService {

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private CommonConfigService commonConfigService;

    @Autowired
    private WazuhControl wazuhControl;

    public ListObjServiceResult pageLogFilesByElementId(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(pageRequest);
        if (!csceneCommonService.checkCsceneIsRunning(pageRequest.getSceneId())) {
            return new ListObjServiceResult(new ListObjResult()).asResult();
        }

        List<String> agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(pageRequest.getSceneId(), pageRequest.getElementIds());

        commonConfigService.getSystemTypeToAgentSupport();

//        ListPageInfoQueryParams listPageInfoQueryParams = new ListPageInfoQueryParams();
//        listPageInfoQueryParams.setPage(pageRequest.getPage());
//        listPageInfoQueryParams.setPageSize(pageRequest.getPageSize());
//        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(CollectionUtil.toList(pageRequest.getSceneId())));
//        listPageInfoQueryParams.setNodeIds(pageRequest.getElementIds());
//        ListObjServiceResult<WazuhNetworkConnectionModel> netConns = scenePluginCollectionSceneService.listNetworkConnections(listPageInfoQueryParams).asResult();


        return null;

    }


}
