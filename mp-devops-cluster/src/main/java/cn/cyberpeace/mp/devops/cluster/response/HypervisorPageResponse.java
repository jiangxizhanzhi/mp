package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "节点管理分页返回信息")
public class HypervisorPageResponse extends BaseOpenStackResponse{

    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String hypervisorHostname;

    /**
     * 节点IP
     */
    @ApiModelProperty("节点IP")
    private String hostIp;

    /**
     * 服务状态
     */
    @ApiModelProperty("服务状态")
    private String status;

    /**
     * 节点管理
     */
    @ApiModelProperty("节点管理")
    private String state;

    /**
     * CPU
     */
    @ApiModelProperty("CPU使用率")
    private Integer vcpus_percent;

    /**
     * 内存
     */
    @ApiModelProperty("内存使用率")
    private Long ram_percent;

    /**
     * 磁盘
     */
    @ApiModelProperty("磁盘")
    private Long disk_percent;

    /**
     * 虚拟机数量
     */
    @ApiModelProperty("虚拟机数量")
    private Integer running_vms;

    /**
     * 容器数量
     */
    @ApiModelProperty("容器数量")
    private Integer container_count;


}
