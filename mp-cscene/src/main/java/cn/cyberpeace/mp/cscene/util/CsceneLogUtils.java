package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.devops.log.request.OperationLogRequest;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author maojun
 * @Date 2022/6/23
 */
@Slf4j
public class CsceneLogUtils {

    public static String CREATE_SCENE_CONFIG = "create_scene_config";
    public static String UPDATE_SCENE_CONFIG = "update_scene_config";
    public static String delete_scene_config = "delete_scene_config";
    public static String start_scene = "start_scene";
    public static String delete_scene = "start_scene";
    public static String batch_delete_scenes = "batch_delete_scenes";

    public static void recordSceneLog(String sceneName, OpTypeEnums opType, String desc) {
        try {
            OperationLogRequest logModel = new OperationLogRequest();
            var user = UserCommon.getCurrentUser();
            if (user == null) {
                log.info("failed to record audit log , because user is null , sceneName is {},opType is {},desc is {}", sceneName, opType.ordinal(), desc);
                return;
            }
            logModel.setUserId(user.getId());
            logModel.setUserName(user.getUsername());
            //操作类型
            logModel.setOpModule(OpModuleEnums.SCENES.ordinal());
            logModel.setUserType(LogConstant.USER_TYPE_USER);
            logModel.setOpIp(getIp());
            logModel.setOpType(opType.ordinal());
            logModel.setOpTarget(sceneName);
            logModel.setDescription(desc);
            logModel.setTitle(MessageCommon.get("cscene.oplog.desc." + desc));
            // 记录登录日志
            var operateLogServiceFactory = BeanCommon.getBean(OperateLogServiceFactory.class);
            operateLogServiceFactory.create(logModel, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error("failed to record audit log", e);
        }
    }

    private static String getIp() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return SnUtils.getRealRemoteIp(request);
    }
}
