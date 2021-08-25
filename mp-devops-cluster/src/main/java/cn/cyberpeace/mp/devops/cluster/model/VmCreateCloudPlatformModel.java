package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.Network;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 虚拟机模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class VmCreateCloudPlatformModel extends BaseModel implements Serializable {

    /**
     * 系统名称
     */
    private String name;

    /**
     * 靶标镜像名称
     */
    private String image;

    /**
     * cpu数量
     */
    private Integer cpu;

    /**
     * 虚拟内存大小（MB）
     */
    private Integer ram;

    /**
     * 虚拟磁盘大小（GB）
     */
    private Integer disk;

    /**
     * 用户名称
     */
    private String requestUser;

    /**
     * 指定主机创建
     */
    private String host;

    /**
     * 网络
     */
    private List<Network> networks;
}
