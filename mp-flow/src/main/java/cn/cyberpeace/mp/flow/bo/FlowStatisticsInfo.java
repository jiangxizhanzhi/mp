package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("流量仿真统计对象")
public class FlowStatisticsInfo {

    
    @ApiModelProperty(value = "发送速率L2(单位byte/s)")
    private Long totalTxL2;

    
    @ApiModelProperty(value = "发送速率L1(单位byte/s)")
    private Long totalTxL1;

    
    @ApiModelProperty(value = "总发包数")
    private Long totalPps;

    
    @ApiModelProperty(value = "接收速率(单位byte/s)")
    private Long totalRx;

    
    @ApiModelProperty(value = "阻塞队列大小(单位byte)")
    private Long queueFull;

    
    @ApiModelProperty(value = "丢包率(单位byte/s)")
    private Long dropRate;

    
    @ApiModelProperty(value = "发包总大小(单位byte)")
    private Long obytes;

    
    @ApiModelProperty(value = "发包错误数")
    private Long oerrors;

    
    @ApiModelProperty(value = "实时速率统计表")
    private List<FlowStatisticsDetail> txBpsList;

    
    @ApiModelProperty(value = "实时发包数统计表")
    private List<FlowStatisticsDetail> txPpsList;

    
    @ApiModelProperty(value = "延迟统计表")
    private List<FlowStatisticsDetail> latencyList;

    
    @ApiModelProperty(value = "抖动统计表")
    private List<FlowStatisticsDetail> jitterList;

    
    @ApiModelProperty(value = "发包大小统计表")
    private List<FlowStatisticsDetail> obytesList;

    
    @ApiModelProperty(value = "发包错误数统计表")
    private List<FlowStatisticsDetail> oerrorsList;

}
