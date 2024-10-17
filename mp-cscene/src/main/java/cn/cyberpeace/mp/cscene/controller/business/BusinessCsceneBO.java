package cn.cyberpeace.mp.cscene.controller.business;

import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessCsceneBO {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "场景拓扑配置")
    private BusinessCscene cscene;
    @ApiModelProperty(value = "接口上下文参数")
    private CsceneContextBO context;
}
