package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpCloudPlatform extends BaseCloudPlatformModel{

    private Boolean status;

    private Map<String, BigDecimal> result;
}
