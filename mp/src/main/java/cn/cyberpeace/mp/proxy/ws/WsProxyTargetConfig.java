package cn.cyberpeace.mp.proxy.ws;

import lombok.Data;

@Data
public class WsProxyTargetConfig {
    private String targetUrl;
    private Integer textMessageSizeLimit;
    private Integer binaryMessageSizeLimit;
    private Integer handshakeTimeout;
    private Boolean partialEnabled;
}
