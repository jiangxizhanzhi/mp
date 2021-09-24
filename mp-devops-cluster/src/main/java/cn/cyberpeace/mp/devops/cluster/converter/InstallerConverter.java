package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.InstallerConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstallerConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.InstallerDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.InstallerPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.InstallerRequest;

public interface InstallerConverter extends BaseConverter{

    /**
     * 分页查询
     * @param request
     */
    InstallerConvertPageModel page(InstallerPageRequest request, String... ignoreFields);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    InstallerConvertModel findById(String id);

    /**
     * 新增
     * @param request
     */
    InstallerConvertModel add(InstallerRequest request);

    /**
     * 批量删除
     * @param request
     */
    BaseCloudPlatformModel delete(InstallerDeleteRequest request);
}
