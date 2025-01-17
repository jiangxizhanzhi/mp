// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ProxyConfig.java.
 // File size 595.
 // File create date Tue Sep 24 15:29:09 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.proxy;

import cn.cyberpeace.mp.proxy.http.HttpProxyConfig;
import cn.cyberpeace.mp.proxy.lf.LfProxyConfig;
import cn.cyberpeace.mp.proxy.ws.WsProxyConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

 /**
  * 代理配置
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Configuration
@ConfigurationProperties(prefix = "proxy")
@Data
public class ProxyConfig {

    private List<HttpProxyConfig> http;

    private List<WsProxyConfig> ws;

    private List<LfProxyConfig> lf;

}
