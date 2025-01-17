// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HttpProxyConfig.java.
 // File size 383.
 // File create date Tue Sep 24 15:29:09 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.proxy.http;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

 /**
  * http代理配置
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Data
public class HttpProxyConfig {
    private String name;
    private String url;
    private String targetUrl;
    private String logEnabled;

     /**
      * 启用日志
      *
      * @return {@link String }
      */
     public String getLogEnabled() {
        return StringUtils.isEmpty(logEnabled) ? "false" : logEnabled;
    }
}
