package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl.BaseOpenStackServiceImpl;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.URL_VERSION;

/**
 * 靶标枚举值接口
 * @author wanggang
 */
@Service
@Slf4j
public class TypeService extends BaseOpenStackServiceImpl {

    private static final String TYPE = "/type";

    public TypeService(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    /**
     * 查询所有类型数据
     * @return
     */
    public TypeCloudPlatformModel findL1AndL2AndL3Type(){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + "?type=l3", TypeCloudPlatformModel.class);
    }

    /**
     * 查询类型数据，仅返回L1和L2级类型数据
     * @return
     */
    public TypeL2CloudPlatformModel findL1AndL2Type(){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + "?type=l2", TypeL2CloudPlatformModel.class);
    }

    /**
     * 查询接入协议端口
     * @return
     */
    public AccessProtocolsPortCloudPlatform findAccessProtocolsPort(){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + "?type=access_protocols_port",
                            AccessProtocolsPortCloudPlatform.class);
    }

    /**
     * 查询所有
     * @return
     */
    public TypeIdIntegerCloudPlatformModel findIdIntegerByType(String type){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + "?type=" + type, TypeIdIntegerCloudPlatformModel.class);
    }

    /**
     * 查询所有
     * @return
     */
    public TypeIdStringCloudPlatformModel findIdStringByType(String type){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + "?type=" + type, TypeIdStringCloudPlatformModel.class);
    }
}
