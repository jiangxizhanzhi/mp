package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceModel implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * UUID
     */
    private String uuid;

    /**
     * 系统名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 镜像信息
     */
    private String image;

    /**
     * 云主机类型信息
     */
    private String flavor;

    /**
     * 地址信息
     */
    private List<Address> addresses;

    /**
     * 逻辑卷
     */
    private List<String> volumes;

    /**
     * 初始化脚本
     */
    private String userData;

    /**
     * 元数据信息
     */
    private Object metadata;

    /**
     * 虚拟机状态
     */
    private String status;

    /**
     * 节点名称
     */
    private String hostname;

    /**
     * 实例名称
     */
    private String instanceName;

    /**
     * 用户
     */
    private String user;

    /**
     * 项目
     */
    private String project;

    /**
     * 创建时间
     */
    private String createdAt;
    /**
     * 更新时间
     */
    private String updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Address{

        /**
         * IP地址
         */
        private String ip;

        /**
         * MAC地址
         */
        private String mac;
    }
}
