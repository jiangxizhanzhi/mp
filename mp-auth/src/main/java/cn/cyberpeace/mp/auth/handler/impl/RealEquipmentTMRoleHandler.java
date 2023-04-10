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
 * @CreateTime: 2023/04/10  18:57
 * @Description: 实装管理相关
 * @Version: 1.0
 */
@Service @UrlKey({RoleAuthControlConstant.TM_REALEQUIPMENT_ADD_API_PATH,
    RoleAuthControlConstant.TM_REALEQUIPMENT_UPDATE_API_PATH, RoleAuthControlConstant.TM_REALEQUIPMENT_DELETE_API_PATH})
public class RealEquipmentTMRoleHandler extends AbstractRoleHandler implements RoleHandler {
    @Override public void handleRequest(HttpServletRequest request) throws Exception {
        if (RoleAuthControlConstant.FORBIDDEN_REQUEST.containsRquest(request.getServletPath(), request.getMethod())) {
            throw new MpRuntimeException(AuthErrorCode.PERMISSION_TRAIL_DENIED);
        }
    }
}
