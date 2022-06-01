package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 初始化注册MP信息到云平台
 * @author wanggang
 */
@Service
@Slf4j
public class InitRegisterService extends BaseOpenStackService {

    public InitRegisterService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 注册MP地址
     * @return
     */
    @Async
    public BaseCloudPlatformResult register(String url) {
        Map<String,Object> map = new HashMap<>();
        map.put("ip_port", url);
        BaseCloudPlatformResult pageResult = super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/register_mp",JSONObject.toJSONString(map), BaseCloudPlatformResult.class);
        super.checkResult(pageResult);
        return pageResult;
    }
}
