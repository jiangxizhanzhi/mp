package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 访问协议端口
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessProtocolsPort implements Serializable {

    private String protocol;

    private Integer port;
}
