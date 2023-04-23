package cn.cyberpeace.mp.cscene.report.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CollectedCsceneElementCommonBO<T> {
    @ApiModelProperty(value = "业务场景id")
    private String csceneId;
    @ApiModelProperty(value = "数据类型: node_access_user node_host_state")
    private String callAction;
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "数据体")
    private T data;
}
