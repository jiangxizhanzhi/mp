package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.AccessProtocolsPort;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import cn.cyberpeace.mp.devops.cluster.model.common.TargetType;
import cn.cyberpeace.mp.devops.cluster.model.common.TypeItemModel;
import cn.cyberpeace.mp.devops.cluster.model.common.TypeItemNameModel;
import cn.cyberpeace.mp.devops.cluster.model.common.TypeTreeItemModel;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
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
    public CloudPlatformPageModel<TypeTreeItemModel> findL1AndL2AndL3Type(Boolean isAll){
        String key = cache_prefix_model + "l3:" + isAll + ":" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (CloudPlatformPageModel<TypeTreeItemModel>)cache;
        }
        CloudPlatformItemsPageResult pageResult =  super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + "l3&isAll=" + isAll, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TypeTreeItemModel> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), TypeTreeItemModel.class);
        CloudPlatformPageModel<TypeTreeItemModel> model = new CloudPlatformPageModel();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    /**
     * 查询类型数据，仅返回L1和L2级类型数据
     * @return
     */
    public CloudPlatformPageModel<TargetType> findL1AndL2Type(){
        String key = cache_prefix_model + "l2:" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (CloudPlatformPageModel<TargetType>)cache;
        }
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + "l2", CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TargetType> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), TargetType.class);
        CloudPlatformPageModel<TargetType> model = new CloudPlatformPageModel();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    /**
     * 查询接入协议端口
     * @return
     */
    public CloudPlatformPageModel<AccessProtocolsPort> findAccessProtocolsPort(){
        String key = cache_prefix_model + "access_protocols_port:" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (CloudPlatformPageModel)cache;
        }
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + "access_protocols_port",
                CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<AccessProtocolsPort> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), AccessProtocolsPort.class);
        CloudPlatformPageModel<AccessProtocolsPort> model = new CloudPlatformPageModel();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    /**
     * 查询所有
     * @return
     */
    public  CloudPlatformPageModel<TypeItemModel> findIdIntegerByType(String type,Boolean isAll){
        String key = cache_prefix_model + type + ":" + isAll + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return ( CloudPlatformPageModel<TypeItemModel>)cache;
        }
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + type + "&isAll=" + isAll, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TypeItemModel> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), TypeItemModel.class);
        CloudPlatformPageModel<TypeItemModel> model = new CloudPlatformPageModel();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    /**
     * 查询所有
     * @return
     */
    public CloudPlatformPageModel<TypeItemNameModel> findIdStringByType(String type,Boolean isAll){
        String key = cache_prefix_model + type + ":" + isAll +  ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (CloudPlatformPageModel<TypeItemNameModel>)cache;
        }
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + type  + "&isAll=" + isAll, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TypeItemNameModel> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), TypeItemNameModel.class);
        CloudPlatformPageModel<TypeItemNameModel> model = new CloudPlatformPageModel();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    /**
     * 根据Type查询
     * @param type hw_disk_bus hw_vif_model hw_video_model
     * @return
     */
    public CloudPlatformPageModel<String> findItemsByType(String type){
        String key = cache_prefix_model + type + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (CloudPlatformPageModel<String>)cache;
        }
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + TYPE + QUESTION_TYPE + type, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<String> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), String.class);
        CloudPlatformPageModel<String> model = new CloudPlatformPageModel();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }
}
