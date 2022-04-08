package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

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

    private static final String cache_prefix_model = "devops:";

    public TypeService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    private Cache<String, Object> lfuCache = null;

    private static final Long TIME_OUT = 5 * 60 * 1000L;

    @PostConstruct
    public void init(){
        lfuCache = CacheUtil.newLFUCache(100);
    }

    /**
     * 查询所有类型数据
     * @return
     */
    public TypeCloudPlatformModel findL1AndL2AndL3Type(Boolean isAll){
        String key = cache_prefix_model + "l3:" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (TypeCloudPlatformModel)cache;
        }
        TypeCloudPlatformModel model = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + "l3&isAll=" + isAll, TypeCloudPlatformModel.class);
        if (Objects.nonNull(model) && model.isSuccess())
            lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    /**
     * 查询类型数据，仅返回L1和L2级类型数据
     * @return
     */
    public TypeL2CloudPlatformModel findL1AndL2Type(){
        String key = cache_prefix_model + "l2:" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (TypeL2CloudPlatformModel)cache;
        }
        TypeL2CloudPlatformModel model = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + "l2", TypeL2CloudPlatformModel.class);
        if (Objects.nonNull(model) && model.isSuccess())
            lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    /**
     * 查询接入协议端口
     * @return
     */
    public AccessProtocolsPortCloudPlatform findAccessProtocolsPort(){
        String key = cache_prefix_model + "access_protocols_port:" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (AccessProtocolsPortCloudPlatform)cache;
        }
        AccessProtocolsPortCloudPlatform model = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + "access_protocols_port",
                AccessProtocolsPortCloudPlatform.class);
        if (Objects.nonNull(model) && model.isSuccess())
            lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    /**
     * 查询所有
     * @return
     */
    public TypeIdIntegerCloudPlatformModel findIdIntegerByType(String type){
        String key = cache_prefix_model + type + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (TypeIdIntegerCloudPlatformModel)cache;
        }
        TypeIdIntegerCloudPlatformModel model = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + type, TypeIdIntegerCloudPlatformModel.class);
        if (Objects.nonNull(model) && model.isSuccess())
            lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    /**
     * 查询所有
     * @return
     */
    public TypeIdStringCloudPlatformModel findIdStringByType(String type){
        String key = cache_prefix_model + type + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (TypeIdStringCloudPlatformModel)cache;
        }
        TypeIdStringCloudPlatformModel model = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + type, TypeIdStringCloudPlatformModel.class);
        if (Objects.nonNull(model) && model.isSuccess())
            lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    /**
     * 根据Type查询
     * @param type hw_disk_bus hw_vif_model hw_video_model
     * @return
     */
    public TypeItemsCloudPlatformModel findItemsByType(String type){
        String key = cache_prefix_model + type + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (TypeItemsCloudPlatformModel)cache;
        }
        TypeItemsCloudPlatformModel model = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + type, TypeItemsCloudPlatformModel.class);
        if (Objects.nonNull(model) && model.isSuccess())
            lfuCache.put(key,model,TIME_OUT);
        return model;
    }
}
