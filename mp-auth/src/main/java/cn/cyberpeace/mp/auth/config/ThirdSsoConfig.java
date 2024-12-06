package cn.cyberpeace.mp.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author shgang
 * @Date 2022/8/31
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mp.thirdsso")
public class ThirdSsoConfig {
    private String roleappcode;
    private String roleclientid;
    private String roleclientsecret;
    private String clientid;
    private String clientsecret;
    private String syncclientsecret;
    private String responsetype;
    private String redirecturi;
    private String pageurl;
    private String gettokenurl;
    private String getuserurl;
    private String getcodeurl;
    private String syncroleurl;
    private Boolean synctokencheck;
}