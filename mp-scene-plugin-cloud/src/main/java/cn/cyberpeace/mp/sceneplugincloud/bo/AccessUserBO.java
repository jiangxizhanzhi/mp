package cn.cyberpeace.mp.sceneplugincloud.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccessUserBO {
    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "名称")
    private String name;
}
