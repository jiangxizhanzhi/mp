package cn.cyberpeace.mp.cscene.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 资产统计返回
 * @Author qiaokang
 * @Date：2022/3/16 16:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "资产统计返回")
public class AssetsStatisticsResponse {

    /**
     * 场景id
     */
    @ApiModelProperty("场景id")
    private String sceneId;


    /**
     * 资产总数
     */
    @ApiModelProperty("资产总数")
    private int assetsTotal;

    /**
     * ip总数
     */
    @ApiModelProperty("ip总数")
    private int ipTotal;

    /**
     * 操作系统数量
     */
    @ApiModelProperty("操作系统数量")
    private int operationSystemCount;

    /**
     * 安全设备数量
     */
    @ApiModelProperty("安全设备数量")
    private int safetySystemCount;

    /**
     * 网路设备数量
     */
    @ApiModelProperty("网路设备数量")
    private int networkCount;

    /**
     * 应用系统数量
     */
    @ApiModelProperty("应用系统数量")
    private int applicationSystemCount;



}
