 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InstanceModel.java.
 // File size 2017.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName InstanceModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
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
    private String host;


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

    /**
     * 运行时间
     */
    private Long runningTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Address implements Serializable{

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
