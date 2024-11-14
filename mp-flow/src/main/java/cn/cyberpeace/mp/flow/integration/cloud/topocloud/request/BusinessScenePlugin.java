package cn.cyberpeace.mp.flow.integration.cloud.topocloud.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessScenePlugin {
    @ApiModelProperty(value = "云插件")
    private BusinessSceneCloudPlugin cpcloud;
}
