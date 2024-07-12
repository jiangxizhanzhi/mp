 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ExtEntityAccess.java.
 // File size 1341.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ExtEntityAccess
 * @Description 扩展实体访问信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtEntityAccess implements Serializable {

    /**
     * 接入类型
     */
    @ApiModelProperty("接入类型")
    private String accessType;

    /**
     * 交换机端口
     */
    @ApiModelProperty("交换机端口")
    private String switchPort;

    /**
     * 交换机名称
     */
    @ApiModelProperty("交换机名称")
    private String switchName;

    /**
     * 实体端口名
     */
    @ApiModelProperty("实体端口名")
    private String portName;

    /**
     * IP地址
     */
    @ApiModelProperty("IP地址")
    private String ip;

    /**
     * IMAC地址
     */
    @ApiModelProperty("IMAC地址")
    private String mac;

    /**
     * 管理协议列表
     */
    @ApiModelProperty("管理协议列表")
    private List<AccessProtocol> accessProtocols;
}
