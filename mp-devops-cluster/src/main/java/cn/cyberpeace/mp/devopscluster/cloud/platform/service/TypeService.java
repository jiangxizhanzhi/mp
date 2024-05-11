package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.AccessProtocolsPort;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.TargetType;
import cn.cyberpeace.mp.devopscluster.model.common.TypeItemModel;
import cn.cyberpeace.mp.devopscluster.model.common.TypeItemNameModel;
import cn.cyberpeace.mp.devopscluster.model.common.TypeTreeItemModel;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 靶标枚举值接口
 * @author wanggang
 */
@Service
@Slf4j
public class TypeService extends BaseCpService {

    /**
     * 靶标分类接口
     */
    private static final String DEVICE_TYPE_URL = "/cloud/v1/device/type?type=";

    private static final String TYPE = "/type";

    private static final String QUESTION_TYPE = "?type=";

    private static final String cache_prefix_model = "devops:";

    public TypeService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 查询所有类型数据
     * @return
     */
    public List<TypeTreeItemModel> findL1AndL2AndL3Type(Boolean isAll){
        CloudPlatformItemsPageResult pageResult =  super.sendGet(DEVICE_TYPE_URL + "l3&isAll=" + isAll, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        return JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()), TypeTreeItemModel.class);
    }

    /**
     * 查询类型数据，仅返回L1和L2级类型数据
     * @return
     */
    public List<TargetType> findL1AndL2Type(){
        CloudPlatformItemsPageResult pageResult = super.sendGet(DEVICE_TYPE_URL + "l2", CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        return JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()), TargetType.class);
    }

    /**
     * 查询接入协议端口
     * @return
     */
    public CloudPlatformPageResult<AccessProtocolsPort> findAccessProtocolsPort(){
        CloudPlatformItemsPageResult pageResult = super.sendGet(DEVICE_TYPE_URL + "access_protocols_port",
                CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<AccessProtocolsPort> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()), AccessProtocolsPort.class);
        CloudPlatformPageResult<AccessProtocolsPort> model = new CloudPlatformPageResult();
        model.setList(list);
        return model;
    }

    /**
     * 查询所有
     * @return
     */
    public CloudPlatformPageResult<TypeItemModel> findIdIntegerByType(String type, Boolean isAll){
        CloudPlatformItemsPageResult pageResult = super.sendGet(DEVICE_TYPE_URL + type + "&isAll=" + isAll, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TypeItemModel> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()), TypeItemModel.class);
        CloudPlatformPageResult<TypeItemModel> model = new CloudPlatformPageResult();
        model.setList(list);
        return model;
    }

    /**
     * 查询所有
     * @return
     */
    public CloudPlatformPageResult<TypeItemNameModel> findIdStringByType(String type, Boolean isAll){
        CloudPlatformItemsPageResult pageResult = super.sendGet(DEVICE_TYPE_URL + type  + "&isAll=" + isAll, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TypeItemNameModel> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()), TypeItemNameModel.class);
        CloudPlatformPageResult<TypeItemNameModel> model = new CloudPlatformPageResult();
        model.setList(list);
        return model;
    }

    /**
     * 根据Type查询
     * @param type hw_disk_bus hw_vif_model hw_video_model
     * @return
     */
    public CloudPlatformPageResult<String> findItemsByType(String type){
        CloudPlatformItemsPageResult pageResult = super.sendGet(DEVICE_TYPE_URL + type, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<String> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()), String.class);
        CloudPlatformPageResult<String> model = new CloudPlatformPageResult();
        model.setList(list);
        return model;
    }
}
