package cn.cyberpeace.mp.collectionapi.bo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FlowTarget {
    private List<String> targetIps;
    private Map<String, String> target2Node;
}
