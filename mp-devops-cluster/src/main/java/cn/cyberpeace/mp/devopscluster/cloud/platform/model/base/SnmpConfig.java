 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnmpConfig.java.
 // File size 1022.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName SnmpConfig
 * @Description SNMP配置
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpConfig implements Serializable {

    /**
     * IP
     */
    @ApiModelProperty(value = "IP")
    private String ip;

    /**
     *
     */
    @ApiModelProperty(value = "团体名")
    private String communityName;

    /**
     * 私有KEY
     */
    @ApiModelProperty(value = "私有KEY")
    private String privKey;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 鉴权KEY
     */
    @ApiModelProperty(value = "鉴权KEY")
    private String authKey;
}
