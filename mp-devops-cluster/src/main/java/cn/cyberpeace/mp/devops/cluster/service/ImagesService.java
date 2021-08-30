package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import org.apache.http.HttpResponse;

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
    Device update(String uuid,Device device);

    /**
     * 构建
     */
    void build();

    /**
     * 导出
     */
    HttpResponse export(String uuid);

    /**
     * 根据快速构建查询
     * @param getParams
     * @return
     */
    DeviceCloudPlatformModel pageForQuickCreate(String getParams);
}
