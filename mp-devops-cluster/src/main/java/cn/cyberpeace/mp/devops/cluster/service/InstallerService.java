package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.*;
import org.springframework.web.multipart.MultipartFile;

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
    Installer add(InstallerFormDataCloudPlatformModel installer, List<MultipartFile> multipartFiles) throws IOException;

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
    Boolean deleteByIds(List<String> ids);
}
