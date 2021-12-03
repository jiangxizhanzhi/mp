package cn.cyberpeace.mp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;

import java.io.File;

/**
 * @Description
 * @Author maojun
 * @Date 2021/11/29
 */
@Slf4j
public class MpEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String env = environment.getProperty("spring.profiles.active");
        if (StringUtils.isBlank(env) || !"prod".equals(env)) {
            return;
        }
        String envPath = environment.getProperty("MP_ENV_PATH");
        try {
            if (StringUtils.isNotBlank(envPath) && new File(envPath).exists()) {
                // 使用指定文件的env变量
                var list = new PropertiesPropertySourceLoader().load("mp_env", new FileUrlResource(envPath));
                if (list != null && list.size() != 0) {
                    environment.getPropertySources().addFirst(list.get(0));
                }
            }
        } catch (Exception e) {
            log.error("failed to parse mp default_env.properties file,env path is {}", envPath, e);
        }
    }
}
