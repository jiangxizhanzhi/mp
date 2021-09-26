package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel(description = "场景模板统计数据")
@Data
@AllArgsConstructor
public class CsceneStatBO {
    @ApiModelProperty(value = "场景模板id")
    private String csceneId;
    @ApiModelProperty(value = "终端数量")
    private Integer terminalCount;
    @ApiModelProperty(value = "路由数量")
    private Integer routerCount;
    @ApiModelProperty(value = "防火墙数量")
    private Integer firewallCount;
    @ApiModelProperty(value = "交换机数量")
    private Integer switchCount;
    @ApiModelProperty(value = "测试场景数量")
    private Integer testSceneCount;
}
