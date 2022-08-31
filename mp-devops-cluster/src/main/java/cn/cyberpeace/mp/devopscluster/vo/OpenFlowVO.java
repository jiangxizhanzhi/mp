package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流表基础信息
 */
@Data
@NoArgsConstructor
public class OpenFlowVO {

    /**
     * 动作
     */
    @ApiModelProperty(value = "动作")
    private String actions;

    /**
     * 匹配条件
     */
    @ApiModelProperty(value = "匹配条件")
    private OpenFlowMatchConditionVO openFlowMatchConditionVO;

    /**
     * cookie
     */
    @ApiModelProperty(value = "cookie")
    private String cookie;

    /**
     * 持续时间
     */
    @ApiModelProperty(value = "持续时间")
    private String duration;

    /**
     * 表
     */
    @ApiModelProperty(value = "表")
    private String table;

    /**
     * 包数
     */
    @ApiModelProperty(value = "包数")
    private String nPackets;

    /**
     * 字节数
     */
    @ApiModelProperty(value = "字节数")
    private String nBytes;

    /**
     * 空闲时间
     */
    @ApiModelProperty(value = "空闲时间")
    private String idleAge;

    /**
     * 网桥
     */
    @ApiModelProperty(value = "网桥")
    private String ovsName;

    /**
     * 节点
     */
    @ApiModelProperty(value = "节点")
    private String hostname;



}
