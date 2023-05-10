package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 速率配置
 * @Author: guquan
 * @Date：2023/5/9 13:55
 */
@Data
@ApiModel("速率配置")
public class FlowTaskRateConf {

    /**
     * 发生模式：0 均匀分布、1 随机分布、2 二项分布、3 指数分布、4 高斯分布
     */
    @ApiModelProperty("发生模式：0 均匀分布、1 随机分布、2 二项分布、3 指数分布、4 高斯分布")
    private Integer loadMode;

    /**
     * 最小速率(单位Mb/s)
     */
    @ApiModelProperty("最小速率(单位Mb/s)")
    private Integer rateMin;

    /**
     * 最大速率(单位Mb/s)
     */
    @ApiModelProperty("最大速率(单位Mb/s)")
    private Integer rateMax;
}
