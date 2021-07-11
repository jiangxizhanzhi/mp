package cn.cyberpeace.mp.cscene.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "资产统计返回")
public class AssetsStatisticsResponse {

    
    @ApiModelProperty("场景id")
    private String sceneId;


    
    @ApiModelProperty("资产总数")
    private int assetsTotal;

    
    @ApiModelProperty("ip总数")
    private int ipTotal;

    
    @ApiModelProperty("操作系统数量")
    private int operationSystemCount;

    
    @ApiModelProperty("安全设备数量")
    private int safetySystemCount;

    
    @ApiModelProperty("网路设备数量")
    private int networkCount;

    
    @ApiModelProperty("应用系统数量")
    private int applicationSystemCount;



}
