 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RouterModel.java.
 // File size 2180.
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
 * @ClassName RouterModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouterModel implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 外部网关信息
     */
    private ExternalGateway externalGateway;

    /**
     * 路由器规则列表
     */
    private List<Routes> routes;

    /**
     *
     */
    private List<Interfaces> interfaces;

    /**
     * 路由器状态
     */
    private String status;

    /**
     * 项目ID
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
    public static final class ExternalGateway implements Serializable{

        /**
         * 外部网络ID
         */
        private String networkId;

        /**
         * 外部网关地址
         */
        private String fixedIp;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Interfaces implements Serializable{

        /**
         * 外部网络ID
         */
        private String networkId;

        /**
         * 外部网关地址
         */
        private String fixedIp;

        /**
         * 端口ID
         */
        private String portId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Routes implements Serializable{

        /**
         * 目标CIDR
         */
        private String destination;

        /**
         * 下一跳
         */
        private String nexthop;
    }
}
