package cn.cyberpeace.mp.devops.cluster.response;

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
public class InstancePageResponse extends BaseCloudPlatformResponse {

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
}
