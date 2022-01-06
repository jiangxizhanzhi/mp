package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Installer;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl.BaseOpenStackServiceImpl;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InstallerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InstallerFormDataCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.URL_VERSION;

/**
 * 靶标管理-服务管理
 * @author wanggang
 */
@Service
@Slf4j
public class InstallerService extends BaseOpenStackServiceImpl {

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
    public InstallerCloudPlatformModel page(String getParams){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + INSTALLER + LISTS + getParams, InstallerCloudPlatformModel.class);
    }

    /**
     * 新增
     * @param installer
     * @return
     */
    public Installer add(InstallerFormDataCloudPlatformModel installer, List<File> fileList) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> installerMap = mapper.readValue(mapper.writeValueAsString(installer), Map.class);
        Set<String> keySet = installerMap.keySet();
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (String key : keySet) {
            if (Objects.nonNull(installerMap.get(key)))
                builder.addPart(TextUtils.humpToLine(key), new StringBody(installerMap.get(key).toString(), ContentType.create("text/plain", Consts.UTF_8)));
        }

        int count = 1;
        if (CollectionUtils.isNotEmpty(fileList)) {
            for (File file : fileList) {
                builder.addBinaryBody("file" + count, file);
                count++;
            }
        }
        if (StringUtils.isNotBlank(installer.getUuid())){
            return super.sendFormDataPut(CPCLOUD_DEVICE + URL_VERSION + INSTALLER, builder.build(), Installer.class);
        }else {
            return super.sendFormDataPost(CPCLOUD_DEVICE + URL_VERSION + INSTALLER + LISTS, builder.build(), Installer.class);
        }
    }

    /**
     * 更新
     * @param uuid
     * @param installer
     * @return
     */
    public Installer update(String uuid,Installer installer){
        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + INSTALLER + "/" + uuid, JsonUtils.toUnderlineJSONString(installer),Installer.class);
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
    public BaseCloudPlatformModel deleteByIds(List<String> ids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", ids);
        return super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + INSTALLER + LISTS,JSONObject.toJSONString(jsonObject),BaseCloudPlatformModel.class);
    }
}
