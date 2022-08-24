package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.config.DevopsConfig;
import cn.cyberpeace.mp.devopscluster.model.ImportDiskSpaceCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 靶标导入相关服务
 * @author wanggang
 */
@Service
@Slf4j
public class ImportService extends BaseCpService {

    private static final String IMPORT = "/import";

    public ImportService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 获取磁盘剩余空间
     * @return
     */
    public ImportDiskSpaceCloudPlatformResult getDiskSpace(){
        ImportDiskSpaceCloudPlatformResult diskSpaceModel = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + IMPORT + "/disk_free", ImportDiskSpaceCloudPlatformResult.class);
        if (Objects.isNull(diskSpaceModel) || Objects.isNull(diskSpaceModel.getData()) || !diskSpaceModel.isSuccess()){
            return new ImportDiskSpaceCloudPlatformResult(new ImportDiskSpaceCloudPlatformResult.DiskSpace(0L));
        }

        return diskSpaceModel;
    }

}
