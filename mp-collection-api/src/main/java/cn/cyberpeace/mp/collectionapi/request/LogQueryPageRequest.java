package cn.cyberpeace.mp.collectionapi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class LogQueryPageRequest {
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "当前页 默认1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页数量 默认10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "设备ip集合")
    private List<String> ips;

    @ApiModelProperty(value = "攻击源ip(战队ip)集合")
    private List<String> attackIps;

    @ApiModelProperty(value = "日志安全等级集合")
    private List<Integer> severity;
}
