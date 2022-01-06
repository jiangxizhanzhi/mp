package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

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
    private String ip;

    /**
     *
     */
    private String communityName;

    /**
     * 私有KEY
     */
    private String privKey;

    /**
     * 用户名
     */
    private String username;

    /**
     * 鉴权KEY
     */
    private String authKey;
}
