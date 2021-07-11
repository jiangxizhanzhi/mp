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
@ApiModel(value = "弹性IP分页返回信息")
public class ElasticIpVO extends BaseVO {

    
    @ApiModelProperty("固定IP")
    private String fixedIpAddress;

    
    @ApiModelProperty("弹性IP")
    private String floatIpAddress;

    
    @ApiModelProperty("状态")
    private String status;

    
    @ApiModelProperty("创建时间")
    private String createdAt;
}
