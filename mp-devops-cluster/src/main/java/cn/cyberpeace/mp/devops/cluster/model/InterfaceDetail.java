package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceDetail {

    private String interfaceName;

    private String status;

    private String linkType;

    private String permitVlan;

    private String hw_addr;

    private String inputTrafficPackets;

    private String inputTrafficBytes;

    private String outputTrafficPackets;

    private String outputTrafficBytes;
}
