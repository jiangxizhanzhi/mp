package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import com.alibaba.fastjson.JSON;
import com.boco.iam.sso.client.LoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.config
 * @Author: hhuang
 * @CreateTime: 2025/03/12  09:57
 * @Description: 单点sso 4A登录配置类
 * @Version: 1.0
 */
@Slf4j
@Configuration
public class SSOFourALoginConfig {

    @Resource
    private SSOConfig ssoConfig;

    /**
     * sso 4a 配置文件
     */
    public static final String SSO_CONFIG_FILE = "sso-client.properties";

    /**
     * 应用标识
     */
    public static final String SSO_4A_CONFIG_APPID = "appid";

    /**
     * 单点登录url
     */
    public static final String SSO_4A_CONFIG_URL = "url";

    /**
     * 登录4a url
     */
    public static final String SSO_4A_CONFIG_LOGIN_URL = "login_4a_url";

    /**
     * 连接超时
     */
    public static final String SSO_4A_CONFIG_CONNECT_TIMEOUT = "conn_timeout";

    /**
     * 读超时
     */
    public static final String SSO_4A_CONFIG_READ_TIMEOUT = "read_timeout";

    @Bean
    public LoginUtil initLoginUtil(){
        log.info("4A loginUtil init start...");
        String path = ssoConfig.getConfigFilePath() + SSO_CONFIG_FILE;
        File file = new File(path);
        if(!file.exists()){
            File dir = new File(ssoConfig.getConfigFilePath());
            try {
                if(!dir.exists() || !dir.isDirectory()){
                    dir.mkdir();
                }
                file.createNewFile();
            } catch (IOException e) {
                log.error("4a sso file create failed!",e);
                throw new RuntimeException(e);
            }
        }
        try (InputStream inputStream = FileUtil.getInputStream(path);FileWriter writer = new FileWriter(path)){
            Properties ssoProp = new Properties();
            ssoProp.load(inputStream);
            ssoProp.setProperty(SSO_4A_CONFIG_APPID,ssoConfig.getAppId());
            ssoProp.setProperty(SSO_4A_CONFIG_URL,ssoConfig.getUrl());
            ssoProp.setProperty(SSO_4A_CONFIG_LOGIN_URL,ssoConfig.getLogin4aUrl());
            ssoProp.setProperty(SSO_4A_CONFIG_CONNECT_TIMEOUT,String.valueOf(ssoConfig.getConnTimeout()));
            ssoProp.setProperty(SSO_4A_CONFIG_READ_TIMEOUT,String.valueOf(ssoConfig.getReadTimeout()));
            ssoProp.store(writer,"store sso config");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LoginUtil loginUtil = LoginUtil.getInstance(path);
        log.info("4A loginUtil init end! loginUtil is {}", JSON.toJSONString(loginUtil));
        return loginUtil;
    }
}
