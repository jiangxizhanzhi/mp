 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FrontAdminConfig.java.
 // File size 562.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/1
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.frontadmin")
public class FrontAdminConfig {

    public static final String DEFAULT_LICENSE_DIR = "/home/mp/config/license";

    private boolean activEnable = false;

    private String licenseDir = DEFAULT_LICENSE_DIR;
}
