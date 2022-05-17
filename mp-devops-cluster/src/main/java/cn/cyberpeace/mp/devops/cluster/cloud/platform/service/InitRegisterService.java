package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.init.ServerConfigListener;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ServerConfigListener serverConfigListener;

    public InitRegisterService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 注册MP地址
     * @return
     */
    public BaseCloudPlatformModel register(String url) {
        Map<String,Object> map = new HashMap<>();
        map.put("ip_port", StringUtils.isBlank(url) ? serverConfigListener.getUrl() : url);
        BaseCloudPlatformModel pageResult = super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/register_mp",JSONObject.toJSONString(map),BaseCloudPlatformModel.class);
        super.checkResult(pageResult);
        return pageResult;
    }
}
