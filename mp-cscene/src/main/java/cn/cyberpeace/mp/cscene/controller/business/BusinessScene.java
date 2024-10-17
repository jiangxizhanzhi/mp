package cn.cyberpeace.mp.cscene.controller.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessScene {
    @ApiModelProperty(value = "名称")
    private String name;
}
