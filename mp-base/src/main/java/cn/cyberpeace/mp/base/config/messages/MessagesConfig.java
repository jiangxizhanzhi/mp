package cn.cyberpeace.mp.base.config.messages;

import cn.cyberpeace.mp.base.util.ModuleUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MessagesConfig {

    /**
     * 国际化扫描路径自动配置，取group下一级包名为模块下国际化文件名称
     * 约定：group下一级包名一一对应模块名称，这样不同的模块国际化文件名称各不相同，否则同名国际化文件会被覆盖
     * @return MessageSource bean
     */
    @Bean
    public MessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        List<String> basenames = new ArrayList<>();
        for (String moduleName: ModuleUtils.MODULE_NAMES) {
            basenames.add("classpath:i18n/" + moduleName);
        }
        messageSource.setBasenames(basenames.toArray(new String[0]));
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
