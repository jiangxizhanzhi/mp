package cn.cyberpeace.mp.collectionapi.BO;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FlowTarget {
    private List<String> targetIps;
    private Map<String, String> target2Node;
    private List<String> destinationIp;
}
