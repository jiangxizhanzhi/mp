package cn.cyberpeace.mp.flow.bo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("所有流量仿真统计对象")
public class FlowStatisticsAll {

    
    @ApiModelProperty(value = "概况信息")
    private FlowSummary flowSummary;

    
    @ApiModelProperty(value = "统计信息")
    private FlowStatisticsInfo flowStatisticsInfo;

    
    @ApiModelProperty(value = "环境总速率")
    private List<FlowStatisticsDetail> totalRateList;

    
    @ApiModelProperty(value = "TOP5实时速率")
    private List<FlowNodeTxBps> top5NodeTxBpsList;

    
    @ApiModelProperty(value = "TOP5流量协议")
    private JSONArray top5Protocols;

}
