package cn.cyberpeace.mp.auth.handler.impl;

import cn.cyberpeace.mp.auth.annotation.UrlKey;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.RoleAuthControlConstant;
import cn.cyberpeace.mp.auth.handler.AbstractRoleHandler;
import cn.cyberpeace.mp.auth.handler.RoleHandler;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.handler.impl
 * @Author: hhuang
 * @CreateTime: 2023/03/25  11:51
 * @Description: 通用处理角色请求权限类
 * @Version: 1.0
 */
@Service
@UrlKey({RoleAuthControlConstant.SYS_CONFIG_UPDATEALL_API_PATH,RoleAuthControlConstant.RUNTIMELOG_DOWNLOAD_API_PATH,
        RoleAuthControlConstant.SYS_NOTICE_API_PATH,RoleAuthControlConstant.CPMONITOR_API_PATH})
public class DefaultRoleHandler extends AbstractRoleHandler implements RoleHandler {
    @Override
    public void handleRequest(HttpServletRequest request){
        //请求为配置中角色禁用的请求则提示无操作权限
        if(RoleAuthControlConstant.FORBIDDEN_REQUEST.containsRquest(request.getServletPath(), request.getMethod())){
            throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
        }
    }
}
