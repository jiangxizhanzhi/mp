package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.sso.SSOLoginBO;
import cn.cyberpeace.mp.auth.bo.sso.SSOLoginOutBO;

/*
 * @BelongsPackage: cn.cyberpeace.mp.auth.service
 * @Author: hhuang
 * @CreateTime: 2025/3/13 16:00
 * @Description: 单点服务接口
 * @Version: 1.0
 */
public interface SSOService {
    
    void login(SSOLoginBO ssoLoginBO);
    
    void logout(SSOLoginOutBO ssoLoginOutBO);

}
