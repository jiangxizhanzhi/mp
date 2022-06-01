package cn.cyberpeace.mp.devops.cluster.vo;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.Data;

/**
 * @Description: 云平台录屏使用空间返回
 * @Author qiaokang
 * @Date：2021/9/13 19:47
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
    private double diskUsage;

    /**
     * 录屏使用空间
     */
    private long recordDirSize;

    /**
     * 查询结果是否成功
     */
    private boolean success;

    /**
     * 请求id
     */
    private String requestId;

}
