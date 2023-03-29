package cn.cyberpeace.mp.devopscluster.config;

import cn.cyberpeace.mp.auth.config.IOperateResource;
import cn.cyberpeace.mp.auth.config.OperateAuth;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

@Component("imageOperateResource")
public class ImageOperateResouce implements IOperateResource {

    @Resource
    private DeviceTargetMapper deviceTargetMapper;

    @Override
    public int resourceType(Object[] args, OperateAuth opearte) {
        try {
            var resource = args[opearte.resourceParam()];
            Field x1 = opearte.resourceClass().getDeclaredField(opearte.resourceField());
            x1.setAccessible(true);
            var param = x1.get(resource);
            if (opearte.resourceFieldType().equals("string")) {
                String id = (String) param;
                var device = deviceTargetMapper.findByUuid(id);
                if (device == null) {
                    var list = deviceTargetMapper.findByIds(List.of(id));
                    if (list != null && list.size() > 0) {
                        device = list.get(0);
                    }
                }
                if (device != null) {
                    if (StringUtils.isEmpty(device.getCreateUser())) {
                        throw new MpRuntimeException(AuthErrorCode.PERMISSION_DENIED);
                    }
                    // createUser 和 当前用户是同一个组织下的用户 允许操作

                }
                return 0;
            } else if (opearte.resourceFieldType().equals("list")) {
                List<String> ids = (List<String>) param;
                var targets = deviceTargetMapper.findByIds(ids);
                // 判断每一个镜像是否有权限
                int abc = 0;
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
