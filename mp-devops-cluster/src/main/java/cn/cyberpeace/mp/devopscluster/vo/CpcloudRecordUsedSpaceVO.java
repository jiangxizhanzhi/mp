package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.Data;


@Data
public class CpcloudRecordUsedSpaceVO extends BaseCloudPlatformResult {

    
    private long diskTotal;

    
    private double recordDiskUsage;

    
    private long recordDirSize;

    
    private long otherUseSize;

    
    private double otherUsePercent;

    
    private boolean success;

    
    private String requestId;

}
