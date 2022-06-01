package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpCloudPlatform extends BaseCloudPlatformResult {

    private Boolean status;

    private Map<String, BigDecimal> result;
}
