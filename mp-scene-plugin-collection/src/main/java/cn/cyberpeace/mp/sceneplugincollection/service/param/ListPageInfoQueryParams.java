package cn.cyberpeace.mp.sceneplugincollection.service.param;

import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ListPageInfoQueryParams extends BasePageRequest {
    @ApiModelProperty(value = "代理名称")
    private String agentName;
    @ApiModelProperty(value = "场景id")
    private List<String> sceneIds;

    @ApiModelProperty(value = "节点id")
    private List<String> nodeIds;

    @ApiModelProperty(value = "场景关闭情况下是否仍然查询数据，默认false，表示不查询")
    private Boolean fetchIfSceneInstanceShutdown = false;

    private List<WazuhEventRequest.TimeRange> timeRanges;

    public ListPageInfoQueryParams() {

    }

    public ListPageInfoQueryParams(String agentName, List<String> sceneIds) {
        this.agentName = agentName;
        this.sceneIds = sceneIds;
    }
}
