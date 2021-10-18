package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpInterfaceResourceCloudPlatform extends BaseCloudPlatformModel{

    private Boolean status;

    private List<Interfaces> result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Interfaces{

        private String interfaceName;

        private Long inTraffic;

        private Long outTraffic;
    }
}
