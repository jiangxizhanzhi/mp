package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("速率配置")
public class FlowTaskRateConf {

    
    @ApiModelProperty("发生模式：0 均匀分布、1 随机分布、2 二项分布、3 指数分布、4 高斯分布")
    private Integer loadMode;

    
    @ApiModelProperty("最小速率(单位Mb/s)")
    private Integer rateMin;

    
    @ApiModelProperty("最大速率(单位Mb/s)")
    private Integer rateMax;
}
