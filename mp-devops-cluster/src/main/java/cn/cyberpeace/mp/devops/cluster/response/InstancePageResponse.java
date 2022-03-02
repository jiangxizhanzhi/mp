package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.vo.FlavorVO;
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
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String node;

    /**
     * Flavor信息
     */
    @ApiModelProperty("Flavor信息")
    private FlavorVO flavor;

    /**
     * IP地址
     */
    @ApiModelProperty("IP地址")
    private List<String> addressList;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private CloudPlatformEnum status;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;
}
