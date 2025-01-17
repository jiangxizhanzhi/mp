// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LfProxyConfig.java.
 // File size 1560.
 // File create date Tue Sep 24 15:29:09 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.proxy.lf;

import cn.hutool.core.io.FileUtil;
import lombok.Data;

 /**
  * 低频代理配置
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Data
public class LfProxyConfig {
    public static final String URL_SEPARATOR = "/";
    public static final String FILE_LOCATION_PREFIX = "file:";
    public static final String CLASSPATH_LOCATION_PREFIX = "classpath:";

    private String name;
    private String url;
    private String location;
    private Boolean urlPatternEnabled;

     /**
      * 获取url
      *
      * @return {@link String }
      */
     public String getUrl() {
        if (Boolean.TRUE.equals(urlPatternEnabled)) {
            return url;
        }

        var formattedUrl = url;
        if (!formattedUrl.endsWith(URL_SEPARATOR)) {
            formattedUrl = formattedUrl + URL_SEPARATOR;
        }
        formattedUrl = formattedUrl + "**";
        return formattedUrl;
    }

     /**
      * 得到位置
      *
      * @return {@link String }
      */
     public String getLocation() {
        var formattedLocation = location;
        if (!(formattedLocation.startsWith(FILE_LOCATION_PREFIX) || formattedLocation.startsWith(CLASSPATH_LOCATION_PREFIX))) {
            formattedLocation = FILE_LOCATION_PREFIX + formattedLocation;
        }
        if (formattedLocation.startsWith(FILE_LOCATION_PREFIX) && !formattedLocation.endsWith(FileUtil.FILE_SEPARATOR)) {
            formattedLocation = formattedLocation + FileUtil.FILE_SEPARATOR;
        }
        if (formattedLocation.startsWith(CLASSPATH_LOCATION_PREFIX) && !formattedLocation.endsWith(URL_SEPARATOR)) {
            formattedLocation = formattedLocation + URL_SEPARATOR;
        }
        return formattedLocation;
    }
}
