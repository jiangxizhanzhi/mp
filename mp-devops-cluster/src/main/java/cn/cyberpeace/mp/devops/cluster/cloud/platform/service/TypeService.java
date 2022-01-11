package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 靶标枚举值接口
 * @author wanggang
 */
@Service
@Slf4j
public class TypeService extends BaseOpenStackService {

    private static final String TYPE = "/type";

    private static final String QUESTION_TYPE = "?type=";

    public TypeService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 查询所有类型数据
     * @return
     */
    public TypeCloudPlatformModel findL1AndL2AndL3Type(){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + "l3", TypeCloudPlatformModel.class);
    }

    /**
     * 查询类型数据，仅返回L1和L2级类型数据
     * @return
     */
    public TypeL2CloudPlatformModel findL1AndL2Type(){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + "l2", TypeL2CloudPlatformModel.class);
    }

    /**
     * 查询接入协议端口
     * @return
     */
    public AccessProtocolsPortCloudPlatform findAccessProtocolsPort(){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + "access_protocols_port",
                            AccessProtocolsPortCloudPlatform.class);
    }

    /**
     * 查询所有
     * @return
     */
    public TypeIdIntegerCloudPlatformModel findIdIntegerByType(String type){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + type, TypeIdIntegerCloudPlatformModel.class);
    }

    /**
     * 查询所有
     * @return
     */
    public TypeIdStringCloudPlatformModel findIdStringByType(String type){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + type, TypeIdStringCloudPlatformModel.class);
    }

    /**
     * 根据Type查询
     * @param type hw_disk_bus hw_vif_model hw_video_model
     * @return
     */
    public TypeItemsCloudPlatformModel findItemsByType(String type){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + type, TypeItemsCloudPlatformModel.class);
    }
}
