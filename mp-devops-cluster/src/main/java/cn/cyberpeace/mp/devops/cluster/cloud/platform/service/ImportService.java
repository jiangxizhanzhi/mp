package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.ImportDiskSpaceCloudPlatformResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 靶标导入相关服务
 * @author wanggang
 */
@Service
@Slf4j
public class ImportService extends BaseOpenStackService {

    private static final String IMPORT = "/import";

    public ImportService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 获取磁盘剩余空间
     * @return
     */
    public ImportDiskSpaceCloudPlatformResult getDiskSpace(){
        ImportDiskSpaceCloudPlatformResult diskSpaceModel = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + IMPORT + "/disk_free", ImportDiskSpaceCloudPlatformResult.class);
        if (Objects.isNull(diskSpaceModel) || Objects.isNull(diskSpaceModel.getData()) || !diskSpaceModel.isSuccess()){
            return new ImportDiskSpaceCloudPlatformResult(new ImportDiskSpaceCloudPlatformResult.DiskSpace(0L));
        }

        return diskSpaceModel;
    }

}
