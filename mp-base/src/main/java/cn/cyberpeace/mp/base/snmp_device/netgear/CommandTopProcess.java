package cn.cyberpeace.mp.base.snmp_device.netgear;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandTopProcess {
    private String pid;
    private String username;
    private String user;
    private String state;
    private String command ;
}
