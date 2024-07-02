package cn.cyberpeace.mp.proxy.ws;

import lombok.Data;

/**
 * @ClassName WsProxyTargetConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class WsProxyTargetConfig {
    private String targetUrl;
    private Integer textMessageSizeLimit;
    private Integer binaryMessageSizeLimit;
    private Integer handshakeTimeout;
    private Boolean partialEnabled;
}
