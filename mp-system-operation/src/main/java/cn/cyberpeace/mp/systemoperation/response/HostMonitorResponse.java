package cn.cyberpeace.mp.systemoperation.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 节点监控返回
 * @Autho qiaokang
 * @Date：2021/7/9 11:10
 */
@Data
@ApiModel("节点监控返回")
public class HostMonitorResponse {

    @ApiModelProperty("节点名称")
    private String node_name;

    @ApiModelProperty("镜像类型")
    private String image_type;

    @ApiModelProperty("虚拟机flavor")
    private String flavor;

    @ApiModelProperty("监控url")
    private String monitor_url;

    @ApiModelProperty("cpu数量")
    private int cpu_count;

    @ApiModelProperty("cpu利用率")
    private BigDecimal cpu_total_percent;

    @ApiModelProperty("cpu利用率")
    private BigDecimal disk_space;

    @ApiModelProperty("总内存")
    private BigDecimal memory_total;

    @ApiModelProperty("空闲内存")
    private BigDecimal memory_free;

    @ApiModelProperty("内存利用率")
    private BigDecimal memory_percent;

}
