package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.*;

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
    Installer add(Installer installer);

    /**
     * 更新
     * @param uuid
     * @param installer
     * @return
     */
    String update(String uuid,Installer installer);

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
    void deleteByIds(List<String> ids);
}
