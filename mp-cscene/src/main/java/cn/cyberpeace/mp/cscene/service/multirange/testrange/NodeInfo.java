package cn.cyberpeace.mp.cscene.service.multirange.testrange;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NodeInfo {
    private String nodeId;
    private String nodeName;
    private String id;
}
