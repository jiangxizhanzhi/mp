package cn.cyberpeace.mp.auth.service.init;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.model.AuthenticationConfigModel;
import cn.cyberpeace.mp.auth.repository.AuthenticationConfigMapper;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/6/21 17:53
 */
@Slf4j
@Component
public class AuthInitService implements ApplicationListener<ContextRefreshedEvent>, EnvironmentAware {

    private Environment environment;


    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationConfigMapper authConfigMapper;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String jwtSecret = authConfig.getAuthenticateJwtSecret();
        if (StringUtils.isBlank(jwtSecret)) {
            AuthenticationConfigModel authConfigModel = authConfigMapper.selectFirst();
            if (Objects.isNull(authConfigModel)) {
                jwtSecret = TextUtils.uuid();
                authConfigModel = new AuthenticationConfigModel(jwtSecret);
                boolean success = authConfigMapper.create(authConfigModel);
                if (!success) {
                    log.error("create authConfigModel:{} failed!", authConfigModel);
                    throw new MpRuntimeException(BaseErrorCode.ERROR);
                }
            }

            jwtSecret = authConfigModel.getJwtSecret();
        }

        authConfig.setAuthenticateJwtSecret(jwtSecret);

        // 更新admin的tokenSecret
        /*String env = environment.getProperty("spring.profiles.active");
        if (StringUtils.isNotBlank(env) && "prod".equals(env)) {
            var updateUser = new UserModel();
            updateUser.setId(AuthConstant.ROOT_USER_ID);
            updateUser.initDefault("tokenSecret");
            userService.update(updateUser);
        }*/
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
