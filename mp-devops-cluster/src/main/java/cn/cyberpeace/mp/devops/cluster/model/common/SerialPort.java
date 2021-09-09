package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialPort {

    private Integer ComAliveT;

    private Integer ComLocalP;

    private Integer TcpLinkMax;

    private String Host;

    private Integer InterfaceId;
}
