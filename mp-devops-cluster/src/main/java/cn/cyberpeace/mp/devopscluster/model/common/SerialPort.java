package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


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
