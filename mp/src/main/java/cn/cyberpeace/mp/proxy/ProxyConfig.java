package cn.cyberpeace.mp.proxy;

import cn.cyberpeace.mp.proxy.http.HttpProxyConfig;
import cn.cyberpeace.mp.proxy.lf.LfProxyConfig;
import cn.cyberpeace.mp.proxy.ws.WsProxyConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @ClassName ProxyConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "proxy")
@Data
public class ProxyConfig {

    private List<HttpProxyConfig> http;

    private List<WsProxyConfig> ws;

    private List<LfProxyConfig> lf;

}
