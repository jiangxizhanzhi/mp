package cn.cyberpeace.mp.flow.bo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName FlowStatisticsAll
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel("所有流量仿真统计对象")
public class FlowStatisticsAll {

    /**
     * 概况信息
     */
    @ApiModelProperty(value = "概况信息")
    private FlowSummary flowSummary;

    /**
     * 统计信息
     */
    @ApiModelProperty(value = "统计信息")
    private FlowStatisticsInfo flowStatisticsInfo;

    /**
     * 环境总速率
     */
    @ApiModelProperty(value = "环境总速率")
    private List<FlowStatisticsDetail> totalRateList;

    /**
     * TOP5实时速率
     */
    @ApiModelProperty(value = "TOP5实时速率")
    private List<FlowNodeTxBps> top5NodeTxBpsList;

    /**
     * TOP5流量协议
     */
    @ApiModelProperty(value = "TOP5流量协议")
    private ArrayNode top5Protocols;

}
