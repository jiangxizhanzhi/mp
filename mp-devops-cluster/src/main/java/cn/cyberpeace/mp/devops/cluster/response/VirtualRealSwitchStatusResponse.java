package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 虚实结合交换机状态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "虚实结合交换机状态")
public class VirtualRealSwitchStatusResponse extends BaseOpenStackResponse{

    /**
     * 名称
     */
    private String name;

    /**
     * 管理IP
     */
    private String hostIp;

    /**
     * 节点管理
     */
    private String state;

    /**
     * 端口总数
     */
    private Integer ports;

    /**
     * 已使用端口数
     */
    private Integer usedPorts;
}
