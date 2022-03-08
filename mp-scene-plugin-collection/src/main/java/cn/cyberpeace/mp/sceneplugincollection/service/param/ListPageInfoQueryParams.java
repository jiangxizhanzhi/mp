package cn.cyberpeace.mp.sceneplugincollection.service.param;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import lombok.Data;

import java.util.List;

@Data
public class ListPageInfoQueryParams extends BasePageRequest {
    private String agentName;

    private List<String> sceneIds;

    private List<String> nodeIds;

    public ListPageInfoQueryParams() {

    }

    public ListPageInfoQueryParams(String agentName, List<String> sceneIds) {
        this.agentName = agentName;
        this.sceneIds = sceneIds;
    }
}
