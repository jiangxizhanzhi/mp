package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 容器分页
 * @author wanggang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlatformPageResult extends BaseCloudPlatformResult {

    private CloudPlatformPageData data;
}