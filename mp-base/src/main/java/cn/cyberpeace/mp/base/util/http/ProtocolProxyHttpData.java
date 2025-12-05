package cn.cyberpeace.mp.base.util.http;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProtocolProxyHttpData implements HttpData {
    private String protocol;
    private String host;
    private String port;
    private String username;
    private String password;
}
