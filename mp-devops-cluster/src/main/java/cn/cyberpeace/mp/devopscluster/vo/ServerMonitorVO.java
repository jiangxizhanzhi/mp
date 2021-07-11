package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.converter.bo.HypervisorBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "服务器监控对象")
public class ServerMonitorVO {

    @ApiModelProperty("异常服务数量")
    private int errorCount;

    @ApiModelProperty("服务总数量")
    private int totalCount;

    @ApiModelProperty("告警数量")
    private int alarmCount;

    @ApiModelProperty("服务器监控列表")
    private List<HypervisorBO> serverMonitors = new ArrayList<>();

}
