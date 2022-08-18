package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStatus {

    /**
     * 节点名称
     */
    private String host;

    /**
     * 节点状态
     */
    private boolean status;

}
