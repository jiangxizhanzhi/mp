package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Installer;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.InstallerFormDataCloudPlatformModel;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class InstallerService extends BaseCpService {

    private static final String INSTALLER = "/installer";

    private static final String LISTS = "/lists";

    public InstallerService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public CloudPlatformPageResult<Installer> page(String getParams){
        CloudPlatformItemsPageResult pageResult = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + INSTALLER + LISTS + getParams, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<Installer> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),Installer.class);
        CloudPlatformPageResult<Installer> model = new CloudPlatformPageResult<>();
        model.setList(list);
        model.setTotal(pageResult.getTotal());
        return model;
    }

    
    public Installer add(InstallerFormDataCloudPlatformModel installer, List<File> fileList) throws IOException{
        Map<String, Object> formMap = BeanUtil.beanToMap(installer,true,true);

        Installer result;
        if (StringUtils.isNotBlank(installer.getUuid())){
            result = super.sendFormDataPut(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + INSTALLER, formMap, Installer.class);
        }else {
            result = super.sendFormDataPost(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + INSTALLER + LISTS, formMap, Installer.class);
        }
        super.checkResult(result);
        return result;
    }

    
    public Installer findById(String id){
        return super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + INSTALLER + "/" + id, Installer.class);
    }

    
    public BaseCloudPlatformResult deleteByIds(List<String> ids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", ids);
        BaseCloudPlatformResult result = super.sendDelete(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + INSTALLER + LISTS,JSONObject.toJSONString(jsonObject), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }
}
