package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NpcModel implements Serializable {

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 网口IP
     */
    private String nodeIp;

    /**
     * 控制节点/计算节点
     */
    private String hostname;
}
