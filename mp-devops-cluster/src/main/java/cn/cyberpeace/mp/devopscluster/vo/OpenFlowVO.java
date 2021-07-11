package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class OpenFlowVO {

    
    @ApiModelProperty(value = "动作")
    private String actions;

    
    @ApiModelProperty(value = "匹配条件")
    private OpenFlowMatchConditionVO openFlowMatchConditionVO;

    
    @ApiModelProperty(value = "cookie")
    private String cookie;

    
    @ApiModelProperty(value = "持续时间")
    private String duration;

    
    @ApiModelProperty(value = "表")
    private String table;

    
    @ApiModelProperty(value = "包数")
    private String nPackets;

    
    @ApiModelProperty(value = "字节数")
    private String nBytes;

    
    @ApiModelProperty(value = "空闲时间")
    private String idleAge;

    
    @ApiModelProperty(value = "网桥")
    private String ovsName;

    
    @ApiModelProperty(value = "节点")
    private String hostname;



}
