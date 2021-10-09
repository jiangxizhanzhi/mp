package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.Installer;
import cn.cyberpeace.mp.devops.cluster.model.InstallerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InstallerFormDataCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.InstallerService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * 服务应用管理
 * @author wanggang
 */
@Service
public class InstallerServiceImpl extends BaseOpenStackServiceImpl implements InstallerService {

    public InstallerServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public InstallerCloudPlatformModel page(String getParams) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/installer/lists" + getParams, InstallerCloudPlatformModel.class);
    }

    @Override
    public Installer add(InstallerFormDataCloudPlatformModel installer, List<File> fileList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> installerMap = mapper.readValue(mapper.writeValueAsString(installer), Map.class);
        Set<String> keySet = installerMap.keySet();
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (String key : keySet) {
            if (Objects.nonNull(installerMap.get(key)))
                builder.addPart(BeanUtils.humpToLine(key), new StringBody(installerMap.get(key).toString(), ContentType.create("text/plain", Consts.UTF_8)));
        }

        int count = 1;
        if (CollectionUtils.isNotEmpty(fileList)) {
            for (File file : fileList) {
                builder.addBinaryBody("file" + count, file);
                count++;
            }
        }
        if (StringUtils.isNotBlank(installer.getUuid())){
            return super.sendFormDataPut(CPCLOUD_DEVICE + URL_VERSION + "/installer", builder.build(), Installer.class);
        }else {
            return super.sendFormDataPost(CPCLOUD_DEVICE + URL_VERSION + "/installer/lists", builder.build(), Installer.class);
        }
    }


    @Override
    public Installer update(String uuid, Installer installer) {
        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + "/installer/" + uuid,JsonUtils.toUnderlineJSONString(installer),Installer.class);
    }

    @Override
    public Installer findById(String id) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/installer/" + id, Installer.class);
    }

    @Override
    public BaseCloudPlatformModel deleteByIds(List<String> ids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", ids);
        return super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + "/installer/lists",JSONObject.toJSONString(jsonObject),BaseCloudPlatformModel.class);
    }
}
