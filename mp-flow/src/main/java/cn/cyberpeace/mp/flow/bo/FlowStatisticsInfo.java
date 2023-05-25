package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 流量仿真统计对象
 * @Author: guquan
 * @Date：2023/5/17 10:48
 */
@Data
@ApiModel("流量仿真统计对象")
public class FlowStatisticsInfo {

    /**
     * 发送速率L2(单位byte/s)
     */
    @ApiModelProperty(value = "发送速率L2(单位byte/s)")
    private Long totalTxL2;

    /**
     * 发送速率L1(单位byte/s)
     */
    @ApiModelProperty(value = "发送速率L1(单位byte/s)")
    private Long totalTxL1;

    /**
     * 总发包数
     */
    @ApiModelProperty(value = "总发包数")
    private Long totalPps;

    /**
     * 接收速率(单位byte/s)
     */
    @ApiModelProperty(value = "接收速率(单位byte/s)")
    private Long totalRx;

    /**
     * 阻塞队列大小(单位byte)
     */
    @ApiModelProperty(value = "阻塞队列大小(单位byte)")
    private Long queueFull;

    /**
     * 丢包率(单位byte/s)
     */
    @ApiModelProperty(value = "丢包率(单位byte/s)")
    private Long dropRate;

    /**
     * 实时速率统计表
     */
    @ApiModelProperty(value = "实时速率统计表")
    private List<FlowStatisticsDetail> txBpsList;

    /**
     * 实时发包数统计表
     */
    @ApiModelProperty(value = "实时速率统计表")
    private List<FlowStatisticsDetail> txPpsList;

    /**
     * 延迟统计表
     */
    @ApiModelProperty(value = "实时速率统计表")
    private List<FlowStatisticsDetail> latencyList;

    /**
     * 抖动统计表
     */
    @ApiModelProperty(value = "实时速率统计表")
    private List<FlowStatisticsDetail> jitterList;

}
