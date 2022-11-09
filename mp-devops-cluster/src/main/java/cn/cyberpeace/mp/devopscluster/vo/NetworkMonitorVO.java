package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 网络监控对象
 * @Author zhouxinghuai
 * @Date：2022/11/09 10:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "网络监控对象")
public class NetworkMonitorVO {

    @ApiModelProperty("异常服务数量")
    private int errorCount;

    @ApiModelProperty("服务总数量")
    private int totalCount;

    @ApiModelProperty("告警数量")
    private int alarmCount;

}
