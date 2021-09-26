package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialPort {

    private Integer comAliveTime;

    private Integer comLocalIp;

    private Integer tcpLinkMax;

    private String host;

    private Integer interfaceId;
}
