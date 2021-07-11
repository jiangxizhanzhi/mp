package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "路由分页返回信息")
public class RouterPageVO extends SceneInfoVO {

    
    @ApiModelProperty("网络名称")
    private String name;

    
    @ApiModelProperty("状态")
    private String status;

    
    @ApiModelProperty("创建时间")
    private String createdAt;

}
