package cn.cyberpeace.mp.base.snmp_device.netgear;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommandTopData {
    private Double availableMemory;
    private Double cpuUsage;
    private Double diskUsage;
    private List<CommandTopProcess> processList;
    private List<CommandTopInte> inteList;
    private List<CommandTopPort> portList ;
}
