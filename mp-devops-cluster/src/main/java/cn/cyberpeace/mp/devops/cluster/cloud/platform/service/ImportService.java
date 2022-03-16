package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.ImportDiskSpaceCloudPlatformModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 靶标导入相关服务
 * @author wanggang
 */
@Service
@Slf4j
public class ImportService extends BaseOpenStackService {

    private static final String KNAPSACK_DEVICE = "/knapsack/device";

    private static final String IMPORT = "/import";

    public ImportService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 获取磁盘剩余空间
     * @return
     */
    public ImportDiskSpaceCloudPlatformModel getDiskSpace(){
        ImportDiskSpaceCloudPlatformModel diskSpaceModel = super.sendGet(KNAPSACK_DEVICE + URL_VERSION + IMPORT + "/disk_free", ImportDiskSpaceCloudPlatformModel.class);
        if (Objects.isNull(diskSpaceModel) || Objects.isNull(diskSpaceModel.getData()) || !diskSpaceModel.isSuccess()){
            return new ImportDiskSpaceCloudPlatformModel(new ImportDiskSpaceCloudPlatformModel.DiskSpace(0L, 0L));
        }

        return diskSpaceModel;
    }

}
