package cn.cyberpeace.mp.sceneplugincollection.service.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ListPageInfoQueryParams extends BasePageRequest {
    private String agentName;

    private List<String> sceneIds;

    private List<String> nodeIds;

    @ApiModelProperty(value = "场景关闭情况下是否仍然查询数据，默认false，表示不查询")
    private Boolean fetchIfSceneInstanceShutdown = false;

    public ListPageInfoQueryParams() {

    }

    public ListPageInfoQueryParams(String agentName, List<String> sceneIds) {
        this.agentName = agentName;
        this.sceneIds = sceneIds;
    }
}
