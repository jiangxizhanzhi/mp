package cn.cyberpeace.mp.base.config.messages;

import cn.cyberpeace.mp.base.config.ModuleConfig;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MessagesConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        List<String> basenames = new ArrayList<>();
        for (String moduleName: ModuleConfig.moduleNames) {
            basenames.add("classpath:i18n/" + moduleName);
        }
        messageSource.setBasenames(basenames.toArray(new String[0]));
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
