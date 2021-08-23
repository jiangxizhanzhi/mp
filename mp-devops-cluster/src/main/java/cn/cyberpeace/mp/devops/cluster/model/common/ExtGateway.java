package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExtGateway {

    /**
     * 管理口/Wan口/Lan口
     */
    private Integer portConfig;

    /**
     * 端口名称
     */
    private String portName;

    /**
     * 端口网段
     */
    private String portIps;
}
