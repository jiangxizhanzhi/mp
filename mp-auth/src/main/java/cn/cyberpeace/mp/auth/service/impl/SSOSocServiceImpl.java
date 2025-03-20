package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.sso.SSOLoginBO;
import cn.cyberpeace.mp.auth.bo.sso.SSOLoginOutBO;
import cn.cyberpeace.mp.auth.constant.SSOSysEnum;
import cn.cyberpeace.mp.auth.service.AbstractSSOService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.service.impl
 * @Author: hhuang
 * @CreateTime: 2025/03/13  16:09
 * @Description: soc单点
 * @Version: 1.0
 */
@Slf4j
@Service
public class SSOSocServiceImpl extends AbstractSSOService {

    @Override
    public boolean isSupport(SSOSysEnum sysEnum) {
        return SSOSysEnum.SOC == sysEnum;
    }

    @Override
    public void doLogin(SSOLoginBO ssoLoginBO) {
        log.info("soc sso login ticket is {}, service is {}",ssoLoginBO.getTicket(),ssoLoginBO.getService());
        //todo
    }

    @Override
    public void doLogout(SSOLoginOutBO ssoLoginOutBO) {
        if(StringUtils.isEmpty(ssoLoginOutBO.getTicket())){
            log.warn("soc logout param ticket is empty!");
            return;
        }
        //todo 目前soc无单点登出接口
    }
}
