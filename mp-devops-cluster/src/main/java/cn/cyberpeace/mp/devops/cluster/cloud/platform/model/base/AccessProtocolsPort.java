package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问协议端口
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessProtocolsPort {

    private String protocol;

    private Integer port;
}
