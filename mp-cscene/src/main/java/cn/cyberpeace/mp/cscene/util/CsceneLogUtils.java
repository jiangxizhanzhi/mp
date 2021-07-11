package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.service.CsceneLogService;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.log.request.OperationLogRequest;
import cn.cyberpeace.mp.log.service.OperateLogService;
import cn.cyberpeace.mp.log.utils.SnUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
public class CsceneLogUtils {

    public static String CREATE_SCENE_CONFIG = "create_scene_config";
    public static String UPDATE_SCENE_CONFIG = "update_scene_config";
    public static String DELETE_SCENE_CONFIG = "delete_scene_config";
    public static String BATCH_DELETE_SCENE_CONFIGS = "batch_delete_scene_configs";
    public static String START_SCENE = "start_scene";
    public static String DELETE_SCENE = "delete_scene";
    public static String BATCH_DELETE_SCENES = "batch_delete_scenes";
    public static String CLEAR_SCENE = "clear_scene";
    public static String BATCH_CLEAR_SCENE = "batch_clear_scene";
    public static String START_FLOW = "start_flow";
    public static String BATCH_START_FLOW = "batch_start_flow";
    public static String STOP_FLOW = "stop_flow";
    public static String BATCH_STOP_FLOW = "batch_stop_flow";
    public static String UPDATE_FLOW = "update_flow";
    public static String SCENE_ELEMENT_SUSPEND = "scene_element_suspend";
    public static String SCENE_ELEMENT_RESUME = "scene_element_resume";
    public static String SCENE_ELEMENT_PAUSE = "scene_element_pause";
    public static String SCENE_ELEMENT_UNPAUSE = "scene_element_unpause";
    public static String SCENE_ELEMENT_RECREATE = "scene_element_recreate";
    public static String SCENE_ELEMENT_RESTART = "scene_element_restart";
    public static String SCENE_ELEMENT_SNAPSHOT = "scene_element_snapshot";

    public static String SCENE_SUSPEND = "scene_suspend";
    public static String SCENE_RESUME = "scene_resume";
    public static String SCENE_PAUSE = "scene_pause";
    public static String SCENE_UNPAUSE = "scene_unpause";

    public static void recordSceneConfigLog(CsceneBO csceneBO, OpTypeEnums opType, String desc, CsceneContextBO context) {
        if (csceneBO == null) {
            log.info("cscene bo is null");
            return;
        }
        CsceneLogService csceneLogService = BeanCommon.getBean(CsceneLogService.class);
        try {
            CsceneLogService.LogDetail detail = csceneLogService.getLogDetail(csceneBO.getId(), true);
            OperationLogRequest logModel = getOperationLogRequest(opType, detail.getTarget(), detail.getContent(), desc, context);
            if (logModel == null) {
                return;
            }
            
            var operationLogService = BeanCommon.getBean(OperateLogService.class);
            operationLogService.create(logModel);
        } catch (Exception e) {
            log.error("failed to record audit log", e);
        }
    }

    public static void recordSceneConfigLog(String csceneId, OpTypeEnums opType, String desc, CsceneContextBO context) {
        try {
            CsceneLogService csceneLogService = BeanCommon.getBean(CsceneLogService.class);
            CsceneLogService.LogDetail detail = csceneLogService.getLogDetail(csceneId, true);
            OperationLogRequest logModel = getOperationLogRequest(opType, detail.getTarget(), detail.getContent(), desc, context);
            if (logModel == null) {
                return;
            }
            
            var operationLogService = BeanCommon.getBean(OperateLogService.class);
            operationLogService.create(logModel);
        } catch (Exception e) {
            log.error("failed to record audit log", e);
        }
    }

    public static void recordSceneConfigLog(List<String> csceneIds, OpTypeEnums opType, String desc, CsceneContextBO context) {
        try {
            CsceneLogService csceneLogService = BeanCommon.getBean(CsceneLogService.class);
            CsceneLogService.LogDetail detail = csceneLogService.getLogDetail(csceneIds, true);
            OperationLogRequest logModel = getOperationLogRequest(opType, detail.getTarget(), detail.getContent(), desc, context);
            if (logModel == null) {
                return;
            }
            
            var operationLogService = BeanCommon.getBean(OperateLogService.class);
            operationLogService.create(logModel);
        } catch (Exception e) {
            log.error("failed to record audit log", e);
        }
    }

    public static void recordSceneInstanceLog(CsceneInstanceBO instanceBO, OpTypeEnums opType, String desc, CsceneContextBO context) {
        try {
            if (instanceBO == null) {
                log.info("instance bo is null");
                return;
            }
            recordSceneInstanceLog(instanceBO.getId(), opType, desc, context);
        } catch (Exception e) {
            log.error("failed to record audit log", e);
        }
    }

    public static void recordSceneNodeLog(String csceneId, String nodeId, OpTypeEnums opType, String desc, CsceneContextBO context) {
        try {
            CsceneLogService csceneLogService = BeanCommon.getBean(CsceneLogService.class);
            CsceneLogService.LogDetail detail = csceneLogService.getLogDetail(csceneId, nodeId);
            OperationLogRequest logModel = getOperationLogRequest(opType, detail.getTarget(), detail.getContent(), desc, context);
            if (logModel == null) {
                return;
            }
            
            var operationLogService = BeanCommon.getBean(OperateLogService.class);
            operationLogService.create(logModel);
        } catch (Exception e) {
            log.error("failed to record audit log", e);
        }
    }

    public static void recordSceneFlowLog(String csceneId, String nodeId, String flowId, OpTypeEnums opType, String desc, CsceneContextBO context) {
        try {
            CsceneLogService csceneLogService = BeanCommon.getBean(CsceneLogService.class);
            CsceneLogService.LogDetail detail = csceneLogService.getLogDetail(csceneId, nodeId, flowId);
            OperationLogRequest logModel = getOperationLogRequest(opType, detail.getTarget(), detail.getContent(), desc, context);
            if (logModel == null) {
                return;
            }
            
            var operationLogService = BeanCommon.getBean(OperateLogService.class);
            operationLogService.create(logModel);
        } catch (Exception e) {
            log.error("failed to record audit log", e);
        }
    }

    public static void recordSceneInstanceLog(String csceneId, OpTypeEnums opType, String desc, CsceneContextBO context) {
        try {
            CsceneLogService csceneLogService = BeanCommon.getBean(CsceneLogService.class);
            CsceneLogService.LogDetail detail = csceneLogService.getLogDetail(csceneId, false);
            OperationLogRequest logModel = getOperationLogRequest(opType, detail.getTarget(), detail.getContent(), desc, context);
            if (logModel == null) {
                return;
            }
            
            var operationLogService = BeanCommon.getBean(OperateLogService.class);
            operationLogService.create(logModel);
        } catch (Exception e) {
            log.error("failed to record audit log", e);
        }
    }

    public static void recordSceneInstanceLog(List<String> csceneIds, OpTypeEnums opType, String desc, CsceneContextBO context) {
        try {
            CsceneLogService csceneLogService = BeanCommon.getBean(CsceneLogService.class);
            CsceneLogService.LogDetail detail = csceneLogService.getLogDetail(csceneIds, false);
            OperationLogRequest logModel = getOperationLogRequest(opType, detail.getTarget(), detail.getContent(), desc, context);
            if (logModel == null) {
                return;
            }
            
            var operationLogService = BeanCommon.getBean(OperateLogService.class);
            operationLogService.create(logModel);
        } catch (Exception e) {
            log.error("failed to record audit log", e);
        }
    }

    private static OperationLogRequest getOperationLogRequest(OpTypeEnums opType, String target, String content, String desc, CsceneContextBO context) {
        OperationLogRequest logModel = new OperationLogRequest();
        UserModel user = null;
        if (context != null) {
            UserService userService = BeanCommon.getBean(UserService.class);
            user = userService.getUserDtoNoRestrict(context.getAccessUserId());
        }
        if (user == null) {
            user = UserCommon.getCurrentUser();
            if (user == null) {
                String userName = getUserName();
                String userId = getUserId();
                if (StringUtils.isNotEmpty(userName)) {
                    user = new UserModel();
                    user.setUsername(userName);
                    user.setId(userId);
                }
                if (user == null) {
                    log.info("failed to record audit log , because user is null , opType is {},desc is {},context is {}", opType.ordinal(), desc, JsonCommon.stringify(context));
                    return null;
                }
            }
        }
        logModel.setUserId(user.getId());
        logModel.setUserName(user.getUsername());
        
        logModel.setOpModule(OpModuleEnums.SCENES.ordinal());
        logModel.setUserType(LogConstant.USER_TYPE_USER);
        logModel.setOpIp(getIp());
        logModel.setOpType(opType.ordinal());
        logModel.setOpTarget(target);
        logModel.setDescription(content);
        logModel.setTitle(MessageCommon.get("cscene.oplog.desc." + desc));
        String operateContent = MessageCommon.get(opType.getCode()) + ":[" + target + "]";
        logModel.setContent(operateContent);
        return logModel;
    }

    private static OperationLogRequest getOperationLogRequest(OpTypeEnums opType, OpTargetEnums opTargetEnums, String desc, CsceneContextBO context) {
        OperationLogRequest logModel = new OperationLogRequest();
        UserModel user = null;
        if (context != null) {
            UserService userService = BeanCommon.getBean(UserService.class);
            user = userService.getUserDtoNoRestrict(context.getAccessUserId());
        }
        if (user == null) {
            log.info("failed to record audit log , because user is null , opType is {},desc is {},context is {}", opType.ordinal(), desc, JsonCommon.stringify(context));
            return null;
        }
        logModel.setUserId(user.getId());
        logModel.setUserName(user.getUsername());
        
        logModel.setOpModule(OpModuleEnums.SCENES.ordinal());
        logModel.setUserType(LogConstant.USER_TYPE_USER);
        logModel.setOpIp(getIp());
        logModel.setOpType(opType.ordinal());
        logModel.setOpTarget(MessageCommon.get(opTargetEnums.getCode()));
        logModel.setDescription(desc);
        logModel.setTitle(MessageCommon.get("cscene.oplog.desc." + desc));
        return logModel;
    }

    private static String getIp() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return SnUtils.getRealRemoteIp(request);
    }

    private static String getUserName() {
        
        return "admin";
    }

    private static String getUserId() {
        
        return "admin";
    }
}
