package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.ContainerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.ContainerCreateCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.ContainerService;
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
public class ContainerServiceImpl extends BaseOpenStackServiceImpl implements ContainerService {

    public ContainerServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig,objectMapper);
    }

    @Override
    public ContainerCloudPlatformModel page(String getParams) {
        return super.sendGet(URL_VERSION + "/containers" + getParams, ContainerCloudPlatformModel.class);
    }

    @Override
    public void create(ContainerCreateCloudPlatformModel model) {
        try {
            super.sendPost(URL_VERSION + "/scenarios/container", JsonUtils.toUnderlineJSONString(model));
        } catch (JsonProcessingException e) {
            return;
        }
    }
}
