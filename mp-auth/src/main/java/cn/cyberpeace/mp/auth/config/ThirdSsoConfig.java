package cn.cyberpeace.mp.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
    // 第三方登录成功之后的页面跳转地址
    private String loginSuccessRedirectUrl;
    // 第三方登录页地址
    private String thirdLoginPageUrl;
    // 第三方ticket校验地址
    private String ticketCheckUrl;
    // 第三方登出地址
    private String thirdLoginOutUrl;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();
        return restTemplate;
    }
}