package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问模式
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessMode {

    /**
     * 接入协议 : [ ssh, rdp, nla, vnc, telnet, serial ]
     */
    private String protocol;

    /**
     * IP V4地址
     */
    private String ipAddress;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * MAC地址
     */
    private String macAddress;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否启用端口映射
     */
    private Boolean enablePortMapping;

    /**
     * 端口映射使用的协议 : [ tcp, udp ]
     */
    private String portMappingProtocol;
}
