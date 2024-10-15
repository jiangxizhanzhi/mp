 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImagesService.java.
 // File size 8131.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtImageInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtVm;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.CloudResponse;
import cn.cyberpeace.mp.devopscluster.model.DeviceQuickCreateCloudPlatformModel;
import cn.cyberpeace.mp.devopscluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devopscluster.vo.TemplateVO;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName ImagesService
 * @Description 镜像管理底层接口
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
public class ImagesService extends BaseCpService {

    private static final String ADD_NEW_DEVICE = "/cloud/v1/device/virtual_device";
    private static final String COPY_NEW_DEVICE = "/cloud/v1/device/create_device_from_instance";
    private static final String UPDATE_DEVICE = "/cloud/v1/device/virtual_device/{uuid}";
    private static final String EXPORT_DEVICE = "/cloud/v1/device/export?device_uuid={device_uuid}";
    private static final String QUICK_CREATE = "/cloud/v1/device/quick_create";
    private static final String UPLOAD_FILE = "/cloud/v1/device/upload?type={type}";

    private static final String DEVICE_CONNECT = "/cloud/v1/device/connection";
    private static final String LIST_TEMPLATE = "/cloud/v1/template/no_register";


    /**
     * ImagesService
     *
     * @param devopsClusterConfig
     */
    public ImagesService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * add
     *
     * @param device
     * @return 靶标信息
     */
    public Device add(Device device) {
        CloudPlatformResult res = super.sendPost(ADD_NEW_DEVICE, JsonUtils.snakeStringify(device), CloudPlatformResult.class);
        super.checkResult(res);
        if (res.getData() != null && (((Map<String, Object>) res.getData()).get("success") != null)) {
            Boolean success = (Boolean) ((Map<String, Object>) res.getData()).get("success");
            if (!success && (((Map<String, Object>) res.getData()).get("message") != null)) {
                String message = (String) ((Map<String, Object>) res.getData()).get("message");
                throw new MpRuntimeException(DevopsClusterErrorCode.REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR.format(message));
            }
        }
        return JsonCommon.snakeParse(JsonCommon.snakeStringify(res.getData()), Device.class);
    }

    public Device copy(Device device) {
        Device res = super.sendPost(COPY_NEW_DEVICE, JsonUtils.snakeStringify(device), Device.class);
        return res;
    }

    /**
     * update
     *
     * @param uuid
     * @param device
     * @return 靶标信息
     */
    public Device update(String uuid, Device device) {
        final ExtVm extVm = device.getExtVm();
        //此状态MP只可查看，不可更新
        if (Objects.nonNull(extVm)) {
            extVm.setInstanceState(null);
            extVm.setBehaveSupport(device.getBehaveSupport());
        }
        final ExtDocker extDocker = device.getExtDocker();
        //此状态MP只可查看，不可更新
        if (Objects.nonNull(extDocker)) {
            extDocker.setInstanceState(null);
        }
        final ExtImageInfo extImageInfo = device.getExtImageInfo();
        if (Objects.nonNull(extImageInfo)) {
            extImageInfo.setDeviceActions(null);
        }

        String api = UriComponentsBuilder.fromPath(UPDATE_DEVICE).buildAndExpand(uuid).toUriString();

        CloudPlatformResult res = super.sendPut(api, JsonUtils.snakeStringify(device), CloudPlatformResult.class);
        super.checkResult(res);
        Device result = JsonCommon.snakeParse(JsonCommon.snakeStringify(res.getData()), Device.class);
        if (null != result.getExtVm()){
            result.setBehaveSupport(result.getExtVm().getBehaveSupport());
        }
        return result;
    }

    /**
     * export
     *
     * @param uuid
     * @param imageName
     * @return
     */
    public String export(String uuid, String imageName) {
        String api = UriComponentsBuilder.fromPath(EXPORT_DEVICE).buildAndExpand(uuid).toUriString();


        CloudPlatformResult result = super.sendGet(api, 10 * 60 * 1000, CloudPlatformResult.class);
        super.checkResult(result);
        List<String> urls = JsonCommon.parse(JsonCommon.snakeStringify(result.getData()), new TypeReference<List<String>>() {
        });
        if (CollectionUtils.isNotEmpty(urls)) {
            return urls.get(0);
        } else {
            return "";
        }
    }

    /**
     * 快速构建
     *
     * @param model
     * @return
     */
    public Device quickCreate(DeviceQuickCreateCloudPlatformModel model) {
        Device device = super.sendPost(QUICK_CREATE, JsonUtils.snakeStringify(model), Device.class);
        super.checkResult(device);
        return device;
    }

    /**
     * 上传文件
     *
     * @param fileList
     * @param type     :  image or installer
     * @return
     */
    public BaseCloudPlatformResult upload(List<File> fileList, String type) {
        if (CollectionUtils.isEmpty(fileList)) {
            return null;
        }
        try {
            String api = UriComponentsBuilder.fromPath(UPLOAD_FILE).buildAndExpand(type).toUriString();

            BaseCloudPlatformResult result = super.sendFormDataPost(api, fileList, BaseCloudPlatformResult.class);
            return result;
        } catch (IOException e) {
            log.error("upload file error", e);
        } finally {
            fileList.forEach(File::delete);
        }
        return null;
    }

    /**
     * 根据靶标ID查询连接信息
     *
     * @param deviceUuid
     * @return
     */
    public CloudPlatformPageResult<TargetConnection> connection(String deviceUuid) {


        CloudPlatformItemsPageResult pageResult = super.sendGet(DEVICE_CONNECT +
                "?device_uuid=" + deviceUuid , CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TargetConnection> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), TargetConnection.class);
        CloudPlatformPageResult<TargetConnection> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }

    public List<TemplateVO> listTemplate(){
        var res = super.sendGet(LIST_TEMPLATE, new TypeReference<CloudResponse<TemplateVO>>() {
        });
        return res.getData();
    }
}
