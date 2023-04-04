package cn.cyberpeace.mp.devopscluster.config;

import cn.cyberpeace.mp.auth.config.IOperateResource;
import cn.cyberpeace.mp.auth.config.OperateAuth;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component("authOperateResouce")
public class AuthOperateResouce implements IOperateResource {

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
        }
        return 0;
    }
}
