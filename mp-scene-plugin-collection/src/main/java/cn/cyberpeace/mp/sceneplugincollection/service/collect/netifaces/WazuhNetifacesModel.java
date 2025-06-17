package cn.cyberpeace.mp.sceneplugincollection.service.collect.netifaces;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WazuhNetifacesModel {
    private String agentId;
    private String agentName;
    private String name;
    private String type;
    private String status;
    private String mtu;
    private String mac;
    private String[] ipAddresses;
    private String txPackets;
    private String rxPackets;
    private String txBytes;
    private String rxBytes;
}
