package cn.cyberpeace.mp.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.annotation.PostConstruct;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/20 17:15
 */
@Configuration
public class AuthConfiguration {

    @Autowired
    private MessageSource messageSource;

    @PostConstruct
    public void addBaseNames() {
        ReloadableResourceBundleMessageSource msgSrc = (ReloadableResourceBundleMessageSource) messageSource;
        msgSrc.addBasenames("classpath:i18n/auth_base");
    }


}
