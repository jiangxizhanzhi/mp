package cn.cyberpeace.mp;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.evn.UTF8PropertiesPropertySourceLoader;
import cn.cyberpeace.mp.log.config.EnvConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileUrlResource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Description
 * @Author maojun
 * @Date 2021/11/29
 */
@Slf4j
public class MpEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        EnvConfig.setProfileActive(environment.getProperty("spring.profiles.active"));
        EnvConfig.setAddtionLocation(environment.getProperty("spring.config.additional-location"));
        EnvConfig.setMpEnvPath(environment.getProperty("MP_ENV_PATH"));
        EnvConfig.splitForExtAndProp();

        String envPath = environment.getProperty("MP_ENV_PATH");
        if (StringUtils.isEmpty(envPath)) {
            // 没有指定mp.properties文件的地址，则使用项目中自带的mp.properties文件
            this.loadPropertiesFromResource(environment);
        } else {
            // 指定了mp.properties文件的地址，则使用指定的mp.properties文件
            this.loadPropertiesFromFilePath(envPath, environment);
        }
    }

    private void loadPropertiesFromResource(ConfigurableEnvironment environment) {
        InputStream inputStream = MpEnvironmentPostProcessor.class.getClassLoader()
                .getResourceAsStream("mp.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (Exception e) {
            log.error("failed to load mp.properties from mp/src/main/resources/mp.properties");
            throw new MpRuntimeException(e.getMessage());
        }

        Map<String, Object> source = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            source.put(key, properties.getProperty(key));
        }
        PropertySource<?> propertySource = new MapPropertySource("mp_env", source);
        environment.getPropertySources().addFirst(propertySource);
    }

    private void loadPropertiesFromFilePath(String filePath, ConfigurableEnvironment environment) {
        try {
            // 使用指定文件的env变量
            List<PropertySource<?>> list = new UTF8PropertiesPropertySourceLoader().load("mp_env", new FileUrlResource(filePath));
            log.info("list = {}", list);
            if (list != null && list.size() != 0) {
                environment.getPropertySources().addFirst(list.get(0));
            }
        } catch (Exception e) {
            log.error("failed to parse mp default_env.properties file,env path is {}", filePath, e);
        }
    }
}
