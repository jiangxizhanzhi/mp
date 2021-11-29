package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.model.*;
import org.apache.http.HttpResponse;

import java.io.File;
import java.util.Map;

/**
 * 镜像管理底层接口
 */
public interface ImagesService extends BaseCloudPlatformService {

    String NORMAL = "/normal";

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
     * 导出
     */
    HttpResponse export(String uuid,String imageName);


    Device quickCreate(DeviceQuickCreateCloudPlatformModel model);


    /**
     * 上传文件
     * @param file
     * @param type :  image or installer
     * @return
     */
    BaseCloudPlatformModel upload(Map<String,File> file, String type);

    ImageConnectionCloudPlatform connection(String deviceUuid);
}
