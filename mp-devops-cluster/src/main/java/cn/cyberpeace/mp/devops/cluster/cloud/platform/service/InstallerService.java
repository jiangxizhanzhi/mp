package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Installer;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.InstallerFormDataCloudPlatformModel;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 靶标管理-服务管理
 * @author wanggang
 */
@Service
@Slf4j
public class InstallerService extends BaseCpService {

    private static final String INSTALLER = "/installer";

    private static final String LISTS = "/lists";

    public InstallerService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<Installer> page(String getParams){
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + INSTALLER + LISTS + getParams, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<Installer> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),Installer.class);
        CloudPlatformPageResult<Installer> model = new CloudPlatformPageResult<>();
        model.setList(list);
        model.setTotal(pageResult.getTotal());
        return model;
    }

    /**
     * 新增
     * @param installer
     * @return
     */
    public Installer add(InstallerFormDataCloudPlatformModel installer, List<File> fileList) throws IOException{
        Map<String, Object> formMap = BeanUtil.beanToMap(installer,true,true);

        Installer result;
        if (StringUtils.isNotBlank(installer.getUuid())){
            result = super.sendFormDataPut(CPCLOUD_DEVICE + URL_VERSION + INSTALLER, formMap, Installer.class);
        }else {
            result = super.sendFormDataPost(CPCLOUD_DEVICE + URL_VERSION + INSTALLER + LISTS, formMap, Installer.class);
        }
        super.checkResult(result);
        return result;
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    public Installer findById(String id){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + INSTALLER + "/" + id, Installer.class);
    }

    /**
     * 批量删除
     * @param ids
     */
    public BaseCloudPlatformResult deleteByIds(List<String> ids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", ids);
        BaseCloudPlatformResult result = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + INSTALLER + LISTS,JSONObject.toJSONString(jsonObject), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }
}
