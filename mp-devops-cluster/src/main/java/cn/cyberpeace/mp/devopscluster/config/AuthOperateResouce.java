package cn.cyberpeace.mp.devopscluster.config;

import cn.cyberpeace.mp.auth.config.IOperateResource;
import cn.cyberpeace.mp.auth.config.OperateAuth;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

@Component("authOperateResouce")
public class AuthOperateResouce implements IOperateResource {

    @Resource
    private DeviceTargetMapper deviceTargetMapper;

    @Override
    public int resourceType(Object[] args, OperateAuth opearte) throws IllegalAccessException {
        var resource = args[opearte.resourceParam()];
        Field x1 = this.getField(opearte.resourceClass(), opearte.resourceField());
        x1.setAccessible(true);
        var param = x1.get(resource);
        if (param == null) {
            return 0;
        }
        if (opearte.resourceFieldType().equals("PublicEnum")) {
            PublicEnum publicEnum = (PublicEnum) param;
            if (PublicEnum.ALL_PUBLIC == publicEnum) {
                throw new MpRuntimeException(AuthErrorCode.PERMISSION_TRAIL_DENIED);
            }
            Field UUIDS = this.getField(opearte.resourceClass(), "uuids");
            UUIDS.setAccessible(true);
            var uuids = UUIDS.get(resource);
            if (uuids == null) {
                return 0;
            }
            List<String> paramsUUids = (List<String>) uuids;
            var listTarget = deviceTargetMapper.findByUuids(paramsUUids);
            if (listTarget != null && listTarget.size() > 0) {
                for (var t : listTarget) {
                    if (StringUtils.isEmpty(t.getCreateUser())) {
                        throw new MpRuntimeException(AuthErrorCode.PERMISSION_TRAIL_DENIED);
                    }
                }
            }
        }
        return 0;
    }
}
