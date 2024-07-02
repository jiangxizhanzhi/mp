package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.Data;

/**
 * @ClassName CpcloudRecordUsedSpaceVO
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 */
@Data
public class CpcloudRecordUsedSpaceVO extends BaseCloudPlatformResult {

    /**
     * 总空间
     */
    private long diskTotal;

    /**
     * 使用率
     */
    private double recordDiskUsage;

    /**
     * 录屏使用空间
     */
    private long recordDirSize;

    /**
     * 其他占用空间大小
     */
    private long otherUseSize;

    /**
     * 其他空间占用率
     */
    private double otherUsePercent;

    /**
     * 查询结果是否成功
     */
    private boolean success;

    /**
     * 请求id
     */
    private String requestId;

}
