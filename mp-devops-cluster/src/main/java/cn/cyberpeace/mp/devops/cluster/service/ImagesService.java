package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.ImageCloudPlatformModel;

/**
 * 镜像管理底层接口
 */
public interface ImagesService extends BaseCloudPlatformService  {

    /**
     * 分页查询
     */
    DeviceCloudPlatformModel page(String getParams);

    /**
     * 新增
     */
    Device add(Device device);

    /**
     * 更新
     */
    String update(String uuid,Device device);

    /**
     * 构建
     */
    void build();

    /**
     * 导出
     */
    void export();
}
