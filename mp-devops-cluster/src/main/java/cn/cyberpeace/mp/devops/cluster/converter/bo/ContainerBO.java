package cn.cyberpeace.mp.devops.cluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerBO {

    /**
     * 容器名称
     */
    private String name;

    /**
     * 镜像名称
     */
    private String image;

    /**
     * 节点名称
     */
    private String node;

    /**
     * IP地址
     */
    private List<String> addressList;

    /**
     * vlan ID集合
     */
    private List<Integer> vlanIdList;

    /**
     * 状态
     */
    private String status;
}
