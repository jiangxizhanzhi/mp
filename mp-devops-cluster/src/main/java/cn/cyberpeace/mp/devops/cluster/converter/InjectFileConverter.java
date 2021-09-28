package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.InjectFileListConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.ImageInjectFileDeleteRequest;

public interface InjectFileConverter {

    /**
     * 查询列表
     * @param deviceUuid
     */
    InjectFileListConvertModel list(String deviceUuid);

    /**
     * 删除
     */
    BaseCloudPlatformModel delete(ImageInjectFileDeleteRequest request);
}
