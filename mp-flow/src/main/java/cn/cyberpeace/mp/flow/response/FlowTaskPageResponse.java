package cn.cyberpeace.mp.flow.response;


import cn.cyberpeace.mp.flow.bo.FlowRealTimeRate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("查询流量任务列表响应体")
public class FlowTaskPageResponse {

    
    @ApiModelProperty(value = "任务id")
    private String id;

    
    @ApiModelProperty(value = "名称")
    private String title;

    
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量，2 全流量")
    private Integer interactiveType;

    
    @ApiModelProperty(value = "资源类型：0 pcap包，1 脚本")
    private Integer resourceType;

    
    @ApiModelProperty(value = "目标节点名称")
    private String nodeName;

    
    @ApiModelProperty(value = "目标节点ip")
    private String nodeIp;

    
    @ApiModelProperty(value = "时长(单位ms)")
    private Long duration;

    
    @ApiModelProperty(value = "大小(单位Byte)")
    private Long pcapSize;

    
    @ApiModelProperty(value = "包数(报文数量)")
    private Long pktNum;

    
    @ApiModelProperty(value = "包含协议")
    private String protocols;

    
    @ApiModelProperty(value = "成功执行次数")
    private Integer successCount;

    
    @ApiModelProperty(value = "失败执行次数")
    private Integer errorCount;

    
    @ApiModelProperty(value = "执行次数")
    private Integer totalCount;

    
    @ApiModelProperty(value = "任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止, 4 失败")
    private Integer taskStatus;

    
    @ApiModelProperty(value = "实时速率")
    private List<FlowRealTimeRate> realTimeRateList;
}
