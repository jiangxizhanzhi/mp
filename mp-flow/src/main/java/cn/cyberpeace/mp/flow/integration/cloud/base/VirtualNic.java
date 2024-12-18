package cn.cyberpeace.mp.flow.integration.cloud.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VirtualNic {
    private String networkId;
    private String fixedIp;
    private String cidr;
    private List<AccessMode> accessModes;

    @Getter
    @Setter
    public static class AccessMode {
        private String protocol;
        private Integer port;
        private String username;
        private String password;
    }
}