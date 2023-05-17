package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.service.OperateLogService;
import cn.cyberpeace.mp.log.utils.SnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.util
 * @Author: hhuang
 * @CreateTime: 2023/05/16  10:46
 * @Description: 用户相关日志工具类
 * @Version: 1.0
 */
@Slf4j
public class AuthLogUtils {


    public static void recordAuthLog(OpTypeEnums opType, OpTargetEnums opTargetEnums, String title ,String content, String desc) {
        try {

            OperationLogRequest
                logModel = getOperationLogRequest(opType, opTargetEnums, title, content, desc);
            if (logModel == null) {
                return;
            }
            // 记录登录日志
            var operationLogService = BeanCommon.getBean(OperateLogService.class);
            operationLogService.create(logModel);
        } catch (Exception e) {
            log.error("failed to record audit log", e);
        }
    }

    private static OperationLogRequest getOperationLogRequest(OpTypeEnums opType, OpTargetEnums opTargetEnums, String title, String content, String desc) {
        OperationLogRequest logModel = new OperationLogRequest();
        HttpServletRequest request = ServletUtils.getRequest();
        Object userId = request.getAttribute(LogConstant.USER_ID_KEY);
        Object userName = request.getAttribute(LogConstant.USER_NAME_KEY);
        Object userType = request.getAttribute(LogConstant.USER_TYPE_KEY);
        if (Objects.nonNull(userId)) {
            logModel.setUserId(userId.toString());
        }else{
            log.info("failed to record audit log , because user is null , opType is {}, title is {}, desc is {}", opType.ordinal(), title,desc);
            return null;
        }
        if (Objects.nonNull(userName)) {
            logModel.setUserName(userName.toString());
        }
        if (Objects.nonNull(userType)) {
            logModel.setUserType(userType.toString());
        }
        //操作类型
        logModel.setOpModule(OpModuleEnums.ACCOUNT.ordinal());
        logModel.setOpIp(getIp());
        logModel.setOpType(opType.ordinal());
        logModel.setOpTarget(MessageCommon.get(opTargetEnums.getCode()));
        logModel.setDescription(desc);
        logModel.setTitle(title);
        logModel.setContent(content);
        return logModel;
    }

    private static String getIp() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return SnUtils.getRealRemoteIp(request);
    }

}
