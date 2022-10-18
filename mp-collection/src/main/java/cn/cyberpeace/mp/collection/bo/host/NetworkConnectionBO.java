package cn.cyberpeace.mp.collection.bo.host;

import cn.cyberpeace.mp.collection.bo.base.BaseEventBO;
import lombok.Data;

@Data
public class NetworkConnectionBO extends BaseEventBO {
    /**
     * 进程id
     */
    private String pid;

    /**
     * 状态
     */
    private String status;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 本地ip
     */
    private String localIp;

    /**
     * 本地端口
     */
    private String localPort;

    /**
     * 远程ip
     */
    private String remoteIp;

    /**
     * 远程端口
     */
    private String remotePort;
}
