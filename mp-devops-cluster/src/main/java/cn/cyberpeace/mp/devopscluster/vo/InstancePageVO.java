package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "虚拟机分页返回信息")
public class InstancePageVO extends BaseVO {

    
    @ApiModelProperty("实例对应的云平台的id")
    private String id;

    
    @ApiModelProperty("实例名称")
    private String name;

    
    @ApiModelProperty("场景名称")
    private String sceneName;

    
    @ApiModelProperty("节点名称")
    private String node;

    
    @ApiModelProperty("IP地址")
    private List<String> addressList;

    
    @ApiModelProperty("状态")
    private String status;

    
    @ApiModelProperty("创建时间")
    private String createTime;

    
    @ApiModelProperty("创建者")
    private String userName;

    
    @ApiModelProperty("运行时间")
    private Long runningTime;

    @ApiModelProperty(value = "引用类型")
    private Integer referenceType;

    @ApiModelProperty(value = "场景事件")
    private Integer event;

    @ApiModelProperty(value = "场景事件状态")
    private Integer eventStatus;

    @ApiModelProperty(value = "场景实例状态")
    private Integer sceneStatus;

    @ApiModelProperty(value = "场景实例状态描述")
    private String sceneStatusDesc;

    @ApiModelProperty(value = "状态信息")
    private String processMessage;

}
