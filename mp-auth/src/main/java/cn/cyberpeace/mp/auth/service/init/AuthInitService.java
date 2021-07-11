package cn.cyberpeace.mp.auth.service.init;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class AuthInitService implements ApplicationListener<ContextRefreshedEvent>, EnvironmentAware {

    private Environment environment;


    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String jwtSecret = authConfig.getAuthenticateJwtSecret();
        if (StringUtils.isBlank(jwtSecret)) {
            log.error("jwtSecret is null!");
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }

        authConfig.setAuthenticateJwtSecret(jwtSecret);

        
        
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
