package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.constant.SSOSysEnum;
import cn.cyberpeace.mp.auth.service.impl.SSOFourAServiceImpl;
import cn.cyberpeace.mp.base.common.BeanCommon;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.service
 * @Author: hhuang
 * @CreateTime: 2025/03/13  16:44
 * @Description: sso服务工厂
 * @Version: 1.0
 */
@Component
public class SSOServiceFactory {


    @Resource
    private List<AbstractSSOService> ssoServiceList;


    /**
     * @description: 获取sso服务
     * @param ssoSysEnum 
     * @throws 
     * @return SSOService
     */
    public SSOService getSSOService(SSOSysEnum ssoSysEnum){
        Optional<AbstractSSOService> findOption =
            ssoServiceList.stream().filter(ssoService -> ssoService.isSupport(ssoSysEnum)).findFirst();
        if(findOption.isPresent()){
            return findOption.get();
        }
        //默认4a登录
        return BeanCommon.getBean(SSOFourAServiceImpl.class);
    }
}
