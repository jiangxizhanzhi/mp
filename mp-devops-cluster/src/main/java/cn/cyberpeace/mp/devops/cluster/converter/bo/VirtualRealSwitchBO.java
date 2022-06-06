package cn.cyberpeace.mp.devops.cluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualRealSwitchBO {

    /**
     * 名称
     */
    private String name;

    /**
     * 管理IP
     */
    private String managementIp;

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
