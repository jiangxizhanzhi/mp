package cn.cyberpeace.mp.cscene.response;


import cn.cyberpeace.mp.flow.bo.FlowRealTimeRate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 查询流量任务列表响应体
 * @Author: guquan
 * @Date：2023/5/13 14:27
 */
@Data
@ApiModel("查询流量任务列表响应体")
public class FlowTaskPageResponse {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String title;

    /**
     * 交互类型：0 背景流量，1 攻击流量，2 全流量
     */
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量，2 全流量")
    private Integer interactiveType;

    /**
     * 目标节点名称
     */
    @ApiModelProperty(value = "目标节点名称")
    private String nodeName;

    /**
     * 目标节点ip
     */
    @ApiModelProperty(value = "目标节点ip")
    private String nodeIp;

    /**
     * 时长(单位ms)
     */
    @ApiModelProperty(value = "时长(单位ms)")
    private Long duration;

    /**
     * 大小(单位Byte)
     */
    @ApiModelProperty(value = "大小(单位Byte)")
    private Long pcapSize;

    /**
     * 包数(报文数量)
     */
    @ApiModelProperty(value = "包数(报文数量)")
    private Long pktNum;

    /**
     * 包含协议
     */
    @ApiModelProperty(value = "包含协议")
    private String protocols;

    /**
     * 成功执行次数
     */
    @ApiModelProperty(value = "成功执行次数")
    private Integer successCount;

    /**
     * 失败执行次数
     */
    @ApiModelProperty(value = "失败执行次数")
    private Integer errorCount;

    /**
     * 执行次数
     */
    @ApiModelProperty(value = "执行次数")
    private Integer totalCount;

    /**
     * 任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止, 4 失败
     */
    @ApiModelProperty(value = "任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止, 4 失败")
    private Integer taskStatus;

    /**
     * 实时速率
     */
    @ApiModelProperty(value = "实时速率")
    private List<FlowRealTimeRate> realTimeRateList;
}
