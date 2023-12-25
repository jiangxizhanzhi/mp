package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtImageInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtVm;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.DeviceQuickCreateCloudPlatformModel;
import cn.cyberpeace.mp.devopscluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devopscluster.request.OfflineUpgradeRequest;
import cn.cyberpeace.mp.devopscluster.utils.JsonUtils;
import cn.cyberpeace.mp.devopscluster.utils.URLUtils;
import cn.cyberpeace.mp.log.config.LogConfig;
import cn.cyberpeace.mp.log.constant.LogErrorCode;
import cn.cyberpeace.mp.log.model.CloudPlatformItemsResult;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 镜像管理底层接口
 *
 * @author wanggang
 */
@Service
@Slf4j
public class ImagesService extends BaseCpService {

    private static final String NORMAL = "/normal";

    private static final String UPGRADDE = "/offline_upgrade";

    private static final String DEVICE = "/cpcloud/device/v1/normal";

    @Autowired
    private LogConfig logConfig;

    public ImagesService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 新增
     */
    public Device add(Device device) {
        Device result = super.sendPost(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + NORMAL + "/lists", JsonUtils.toUnderlineJSONString(device), Device.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 更新
     */
    public Device update(String uuid, Device device) {
        final ExtVm extVm = device.getExtVm();
        //此状态MP只可查看，不可更新
        if (Objects.nonNull(extVm)) {
            extVm.setInstanceState(null);
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
        Device result = super.sendPut(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + NORMAL + "/" + uuid, JsonUtils.toUnderlineJSONString(device), Device.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 导出
     */
    public String export(String uuid, String imageName) {
        BaseCloudPlatformResult result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + "/image/export?device_uuid=" + uuid + "&&image_name=" + imageName, 10 * 60 * 1000, BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result.getUrl();
    }

    /**
     * 快速构建
     *
     * @param model
     * @return
     */
    public Device quickCreate(DeviceQuickCreateCloudPlatformModel model) {
        Device device = super.sendPost(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + "/quick_create", JsonUtils.toUnderlineJSONString(model), Device.class);
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
            BaseCloudPlatformResult result = super.sendFormDataPost(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + "/file_upload?type=" + type, fileList, BaseCloudPlatformResult.class);
            super.checkResult(result);
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
        CloudPlatformItemsPageResult pageResult = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + NORMAL + "/connection?device_uuid=" + deviceUuid + "&user_id=" + (Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : ""), CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TargetConnection> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), TargetConnection.class);
        DevopsClusterConfig config = BeanCommon.getBean(DevopsClusterConfig.class);
        if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
            list.forEach(l -> {
                if (StringUtils.isEmpty(config.getPublicNetProxyIp())) {
                    return;
                }
                //采用http、https协议访问节点部署的web服务时，前端是直接用ip+端口号访问，这里url为空
                if (StringUtils.isNotEmpty(l.getUrl())) {
                    var u = URLUtils.url(l.getUrl());
                    l.setUrl(StringUtils.replaceOnce(l.getUrl(), u.getHost(), config.getPublicNetProxyIp()));
                }
                if (config.getPublicNetProxyExcludeProtocols().contains(l.getProtocol())) {
                    return;
                }
                l.setIpAddress(config.getPublicNetProxyIp());
                if (config.getPublicNetProxyPort() != null) {
                    l.setPort(config.getPublicNetProxyPort());
                }
            });
        }
        CloudPlatformPageResult<TargetConnection> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }

    public void OfflineUpgrade(OfflineUpgradeRequest request) {

        //构造请求对象
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonNode = mapper.createObjectNode();


        String filename = request.getUploadFilename();
        String diskFormat = filename.substring(filename.lastIndexOf(".") + 1);

        jsonNode.put("user_filename", filename);
        jsonNode.put("disk_format", diskFormat);
        jsonNode.put("upload_type", request.getUploadType());

        String jsonString = null;
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (Exception e) {
            log.error("JSON conversion failed, {}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(DevopsClusterErrorCode.OFFLINE_UPGRADE_IMAGE.format("JSON conversion failed"));
        }
        String url = DEVICE + "/" + request.getUuid() + UPGRADDE;
        try {
            super.sendPost(url, jsonString);
        } catch (Exception e) {
            log.error("upgrade image error, {}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(DevopsClusterErrorCode.OFFLINE_UPGRADE_IMAGE.format("Upgrade Image Error"));
        }
    }
}
