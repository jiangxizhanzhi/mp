package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 容器分页返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "容器分页返回信息")
public class ContainerPageResponse extends BaseCloudPlatformResponse {

    /**
     * 容器名称
     */
    @ApiModelProperty("容器名称")
    private String name;

    /**
     * 场景名称
     */
    @ApiModelProperty("场景名称")
    private String sceneName;

    /**
     * 镜像名称
     */
    @ApiModelProperty("镜像名称")
    private String image;

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

    @ApiModelProperty("Vlan ID集合")
    private List<Integer> vlanIdList;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;
}
