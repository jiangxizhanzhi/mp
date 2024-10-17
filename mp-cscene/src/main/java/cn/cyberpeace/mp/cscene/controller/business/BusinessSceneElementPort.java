package cn.cyberpeace.mp.cscene.controller.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BusinessSceneElementPort {
    @ApiModelProperty(value = "端口id")
    private String id;
    @ApiModelProperty(value = "端口名称")
    private String name;
    @ApiModelProperty(value = "端口ip")
    private String ip;
    @ApiModelProperty(value = "端口接入方式")
    private List<BusinessSceneElementAccessMode> accessModes;
}

