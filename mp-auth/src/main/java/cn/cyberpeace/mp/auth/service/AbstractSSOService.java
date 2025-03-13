package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.sso.SSOLoginBO;
import cn.cyberpeace.mp.auth.constant.SSOSysEnum;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.service
 * @Author: hhuang
 * @CreateTime: 2025/03/13  16:01
 * @Description: 单点抽象类
 * @Version: 1.0
 */
public abstract class AbstractSSOService implements SSOService{

    @Override
    public void login(SSOLoginBO ssoLoginBO) {
        doLogin(ssoLoginBO);
    }

    @Override
    public void logout() {
        doLogout();
    }

    public abstract boolean isSupport(SSOSysEnum sysEnum);

    public abstract void doLogin(SSOLoginBO ssoLoginBO);

    public abstract void doLogout();
}
