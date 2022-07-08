package cn.cyberpeace.mp.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/8 10:17
 */

@Component
@Data
@ConfigurationProperties(prefix = "mp.auth.biz")
public class AuthBizConfig {

    // 退出登录后是否重置tokenSecret
    private Boolean logoutResetSecret = true;

    // 最大可通过excel导入的用户数量
    private Integer maxImportUserSize;

}
