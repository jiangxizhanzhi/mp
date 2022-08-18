package cn.cyberpeace.mp.devopscluster.model.common;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件注入
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InjectFile extends BaseCloudPlatformResult {

    private String uuid;

    private String name;

    private String destPath;

    private Integer size;

    private String status;
}
