package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialPort implements Serializable {

    private Integer comAliveTime;

    private Integer comLocalIp;

    private Integer tcpLinkMax;

    private String host;

    private Integer interfaceId;
}
