package cn.cyberpeace.mp.devops.cluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 虚拟机分页返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "虚拟机分页返回信息")
public class InstancePageVO extends BaseVO {

    /**
     * 实例名称
     */
    @ApiModelProperty("实例名称")
    private String name;

    /**
     * 场景名称
     */
    @ApiModelProperty("场景名称")
    private String sceneName;


    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String node;

    /**
     * IP地址
     */
    @ApiModelProperty("IP地址")
    private List<String> addressList;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String userName;

    /**
     * 运行时间
     */
    @ApiModelProperty("运行时间")
    private Long runningTime;;

    private Integer referenceType;

    private Integer event;

    private Integer eventStatus;

    private String processMessage;

}
