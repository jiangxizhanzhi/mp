package cn.cyberpeace.mp.auth.handler.impl;

import cn.cyberpeace.mp.auth.annotation.UrlKey;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.RoleAuthControlConstant;
import cn.cyberpeace.mp.auth.constant.RoleAuthControlConstant.FORBIDDEN_REQUEST;
import cn.cyberpeace.mp.auth.handler.AbstractRoleHandler;
import cn.cyberpeace.mp.auth.handler.RoleHandler;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.handler.impl
 * @Author: hhuang
 * @CreateTime: 2023/03/25  11:19
 * @Description: 靶标管理-镜像管理处理
 * @Version: 1.0
 */
@Service
@UrlKey(RoleAuthControlConstant.TM_IMAGES_API_PATH)
public class ImagesManagementTMRoleHandler extends AbstractRoleHandler implements RoleHandler {

    @Override
    public void handleRequest(HttpServletRequest request){
        String servletPath = request.getServletPath();
        //请求为配置中角色禁用的请求则提示无操作权限
        if(FORBIDDEN_REQUEST.containsRquest(servletPath, request.getMethod())){
//            FORBIDDEN_REQUEST forbiddenRequest = FORBIDDEN_REQUEST.getForbiddenRequest(servletPath, request.getMethod());
//            String model = forbiddenRequest.getModel();
//            Method method = ImagesManagementTMRoleHandler.class.getMethod(model, HttpServletRequest.class);
//            method.invoke(this,request);
            throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
        }
    }
}
