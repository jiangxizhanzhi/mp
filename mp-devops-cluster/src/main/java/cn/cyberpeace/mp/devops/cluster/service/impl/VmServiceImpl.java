package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.VmCreateCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.VmService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
@Slf4j
@Service
public class VmServiceImpl extends BaseOpenStackServiceImpl implements VmService {

    public VmServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig,objectMapper);
    }

    @Override
    public InstanceCloudPlatformModel page(String getParams) {
        return super.sendGet(URL_VERSION + "/instances" + getParams, InstanceCloudPlatformModel.class);
    }

    @Override
    public void create(VmCreateCloudPlatformModel vmCreateCloudPlatformModel) {
        try {
            super.sendPost(URL_VERSION + "/scenarios/vm", JsonUtils.toUnderlineJSONString(vmCreateCloudPlatformModel));
        } catch (JsonProcessingException e) {
            return;
        }
    }


}
