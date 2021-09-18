package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.Installer;
import cn.cyberpeace.mp.devops.cluster.model.InstallerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InstallerFormDataCloudPlatformModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 靶标管理-服务管理
 * @author wanggang
 */
public interface InstallerService extends BaseCloudPlatformService {

    /**
     * 分页查询
     * @param getParams
     * @return
     */
    InstallerCloudPlatformModel page(String getParams);

    /**
     * 新增
     * @param installer
     * @return
     */
    Installer add(InstallerFormDataCloudPlatformModel installer, List<File> fileList) throws IOException;

    /**
     * 更新
     * @param uuid
     * @param installer
     * @return
     */
    Installer update(String uuid,Installer installer);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Installer findById(String id);

    /**
     * 批量删除
     * @param ids
     */
    BaseCloudPlatformModel deleteByIds(List<String> ids);
}
