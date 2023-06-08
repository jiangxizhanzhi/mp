package cn.cyberpeace.mp.flow.bo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 所有流量仿真统计对象
 * @Author: guquan
 * @Date：2023/6/8 16:31
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
    private JSONArray top5Protocols;

}
