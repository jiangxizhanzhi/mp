package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devopscluster.model.DeviceToolModel;

import java.util.List;

/**
 * @Description 镜像工具信息service
 * @Author liyah
 * @Date 2025-6-19 10:11
 */
public interface DeviceToolService extends BaseModelService<DeviceToolModel, QueryParams> {

    void updateByDeviceUuid(List<DeviceToolModel> tools, String uuid);
}

