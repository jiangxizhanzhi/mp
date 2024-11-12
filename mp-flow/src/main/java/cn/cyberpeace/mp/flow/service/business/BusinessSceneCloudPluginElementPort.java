package cn.cyberpeace.mp.flow.service.business;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BusinessSceneCloudPluginElementPort {
    @ApiModelProperty(value = "ip")
    private String ip ;
    @ApiModelProperty(value = "接入方式")
    private List<BusinessSceneElementAccessMode> accessModes;
}
