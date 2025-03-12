package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import com.boco.iam.sso.client.LoginUtil;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.config
 * @Author: hhuang
 * @CreateTime: 2025/03/12  09:10
 * @Description: 单点登录核心类型
 * @Version: 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.auth.sso")
public class SSOConfig {

    /**
     * 应用标识
     */
    private String appId;

    /**
     * 单点登录核心服务url
     */
    private String url;

    /**
     * 实际jar包中未使用此配置  login_4a_url = url.substring(0, url.lastIndexOf("/"));
     */
    private String login4aUrl;

    /**
     * 连接超时
     */
    private Integer connTimeout;

    /**
     * 读超时
     */
    private Integer readTimeout;

}
