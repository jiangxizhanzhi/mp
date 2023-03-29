package cn.cyberpeace.mp.cscene.config;

import cn.cyberpeace.mp.auth.config.IOperateResource;
import cn.cyberpeace.mp.auth.config.OperateAuth;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;

@Component("csceneOperateResource")
public class CsceneConfigOperateResouce implements IOperateResource {

    @Resource
    private CsceneMapper csceneMapper;

    @Override
    public int resourceType(Object[] args, OperateAuth opearte) throws IllegalAccessException {
        var resource = args[opearte.resourceParam()];
        Field x1 = this.getField(opearte.resourceClass(), opearte.resourceField());
        x1.setAccessible(true);
        var param = x1.get(resource);
        if (param == null) {
            return 0;
        }
        if (opearte.resourceFieldType().equals("string")) {
            String id = (String) param;
            var cscene = csceneMapper.get(id);
            if (cscene == null) {
                return 0;
            }
            if (StringUtils.isEmpty(cscene.getCreateUser())) {
                throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
            }
            // createUser 和 当前用户是同一个组织下的用户 允许操作
            var userIds = UserUtils.getSameOrgUserIds(UserUtils.getLoginUser().getId());
            if (!userIds.contains(cscene.getCreateUser())) {
                throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
            }
            return 0;
        } else if (opearte.resourceFieldType().equals("array")) {
            String[] ids = (String[]) param;
            var models = csceneMapper.list(new HumanCondition().filterIn(CsceneModel::getId, ids));
            // 判断每一个model是否可操作
            var userIds = UserUtils.getSameOrgUserIds(UserUtils.getLoginUser().getId());
            if (models != null) {
                for (var model : models) {
                    if (!userIds.contains(model.getCreateUser())) {
                        throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
                    }
                }
            }
        }
        return 0;
    }
}
