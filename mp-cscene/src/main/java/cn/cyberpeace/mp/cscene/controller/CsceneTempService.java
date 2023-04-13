package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.auth.config.OperateAuth;
import cn.cyberpeace.mp.cscene.controller.request.CsceneActionRequest;
import org.springframework.stereotype.Component;

/**
 * todo 临时先处理一下,5.4的试用账号里要改下
 */
@Component
public class CsceneTempService {
    @OperateAuth(resourceClass = CsceneActionRequest.class, resourceField = "id", resourceType = "csceneOperateResource")
    public void delete(CsceneActionRequest request) {
        // 为了触发下注解
    }
}
