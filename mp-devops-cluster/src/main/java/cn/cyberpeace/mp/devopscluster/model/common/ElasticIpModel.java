 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ElasticIpModel.java.
 // File size 1496.
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
 * @ClassName ElasticIpModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElasticIpModel implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * 承租者ID
     */
    private String tenantId;

    /**
     * 弹性IP地址
     */
    private String floatingIpAddress;

    /**
     * 弹性网络ID
     */
    private String floatingNetworkId;

    /**
     * 路由ID
     */
    private String routerId;

    /**
     * 端口
     */
    private String portId;

    /**
     * 固定IP地址
     */
    private String fixedIpAddress;

    /**
     * 状态
     */
    private String status;

    /**
     * 描述
     */
    private String description;

    /**
     * 端口详细
     */
    private String portDetails;

    /**
     *
     */
    private List<String> tags;

    /**
     * 修订号
     */
    private Integer revisionNumber;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 创建时间
     */
    private String createdAt;
    /**
     * 更新时间
     */
    private String updatedAt;
}
