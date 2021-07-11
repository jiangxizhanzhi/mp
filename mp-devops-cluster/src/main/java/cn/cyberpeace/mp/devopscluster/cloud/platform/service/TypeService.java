package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.AccessProtocolsPort;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.TargetType;
import cn.cyberpeace.mp.devopscluster.model.common.TypeItemModel;
import cn.cyberpeace.mp.devopscluster.model.common.TypeItemNameModel;
import cn.cyberpeace.mp.devopscluster.model.common.TypeTreeItemModel;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class TypeService extends BaseCpService {

    private static final String TYPE = "/type";

    private static final String QUESTION_TYPE = "?type=";

    private static final String cache_prefix_model = "devops:";

    public TypeService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    private Cache<String, Object> lfuCache = null;

    private static final Long TIME_OUT = 5 * 60 * 1000L;

    @PostConstruct
    public void init(){
        lfuCache = CacheUtil.newLFUCache(100);
    }

    
    public CloudPlatformPageResult<TypeTreeItemModel> findL1AndL2AndL3Type(Boolean isAll){
        String key = cache_prefix_model + "l3:" + isAll + ":" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (CloudPlatformPageResult<TypeTreeItemModel>)cache;
        }
        CloudPlatformItemsPageResult pageResult =  super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + TYPE + QUESTION_TYPE + "l3&isAll=" + isAll, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TypeTreeItemModel> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), TypeTreeItemModel.class);
        CloudPlatformPageResult<TypeTreeItemModel> model = new CloudPlatformPageResult();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    
    public CloudPlatformPageResult<TargetType> findL1AndL2Type(){
        String key = cache_prefix_model + "l2:" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (CloudPlatformPageResult<TargetType>)cache;
        }
        CloudPlatformItemsPageResult pageResult = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + TYPE + QUESTION_TYPE + "l2", CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TargetType> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), TargetType.class);
        CloudPlatformPageResult<TargetType> model = new CloudPlatformPageResult();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    
    public CloudPlatformPageResult<AccessProtocolsPort> findAccessProtocolsPort(){
        String key = cache_prefix_model + "access_protocols_port:" + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (CloudPlatformPageResult)cache;
        }
        CloudPlatformItemsPageResult pageResult = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + TYPE + QUESTION_TYPE + "access_protocols_port",
                CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<AccessProtocolsPort> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), AccessProtocolsPort.class);
        CloudPlatformPageResult<AccessProtocolsPort> model = new CloudPlatformPageResult();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    
    public CloudPlatformPageResult<TypeItemModel> findIdIntegerByType(String type, Boolean isAll){
        String key = cache_prefix_model + type + ":" + isAll + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (CloudPlatformPageResult<TypeItemModel>)cache;
        }
        CloudPlatformItemsPageResult pageResult = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + TYPE + QUESTION_TYPE + type + "&isAll=" + isAll, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TypeItemModel> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), TypeItemModel.class);
        CloudPlatformPageResult<TypeItemModel> model = new CloudPlatformPageResult();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    
    public CloudPlatformPageResult<TypeItemNameModel> findIdStringByType(String type, Boolean isAll){
        String key = cache_prefix_model + type + ":" + isAll +  ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (CloudPlatformPageResult<TypeItemNameModel>)cache;
        }
        CloudPlatformItemsPageResult pageResult = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + TYPE + QUESTION_TYPE + type  + "&isAll=" + isAll, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TypeItemNameModel> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), TypeItemNameModel.class);
        CloudPlatformPageResult<TypeItemNameModel> model = new CloudPlatformPageResult();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }

    
    public CloudPlatformPageResult<String> findItemsByType(String type){
        String key = cache_prefix_model + type + ServletUtils.getAcceptLanguage();
        Object cache = lfuCache.get(key);
        if (Objects.nonNull(cache)){
            return (CloudPlatformPageResult<String>)cache;
        }
        CloudPlatformItemsPageResult pageResult = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + TYPE + QUESTION_TYPE + type, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<String> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), String.class);
        CloudPlatformPageResult<String> model = new CloudPlatformPageResult();
        model.setList(list);
        lfuCache.put(key,model,TIME_OUT);
        return model;
    }
}
