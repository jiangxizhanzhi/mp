package cn.cyberpeace.mp.flow.config;

import cn.cyberpeace.mp.auth.config.IOperateResource;
import cn.cyberpeace.mp.auth.config.OperateAuth;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;

@Component("flowOperateResource")
public class FlowOperateResouce implements IOperateResource {

    @Resource
    private FlowInfoMapper flowInfoMapper;

    @Override
    public int resourceType(Object[] args, OperateAuth opearte) {
        try {
            var resource = args[opearte.resourceParam()];
            Field x1 = this.getField(opearte.resourceClass(), opearte.resourceField());
            x1.setAccessible(true);
            var param = x1.get(resource);
            if (param == null) {
                return 0;
            }
            if (opearte.resourceFieldType().equals("string")) {
                String id = (String) param;
                var flowInfoModel = flowInfoMapper.get(id);
                if (flowInfoModel == null) {
                    return 0;
                }
                if (StringUtils.isEmpty(flowInfoModel.getCreateUser())) {
                    throw new UnsupportedOperationException();
                }
                // createUser 和 当前用户是同一个组织下的用户 允许操作
                return 0;
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
