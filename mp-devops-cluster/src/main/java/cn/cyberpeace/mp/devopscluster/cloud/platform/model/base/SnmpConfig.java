package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * SNMP配置
 * @author wanggang
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
    @ApiModelProperty(value = "")
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
