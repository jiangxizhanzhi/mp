package cn.cyberpeace.mp.devopscluster.converter.bo;

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
     * 实例对应的云平台的id
     */
    private String id;

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

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 创建者
     */
    private String userName;

    /**
     * 运行时间
     */
    private Long runningTime;
}
