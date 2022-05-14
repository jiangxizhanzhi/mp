package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineGetResult implements CloudHttpData {
    private String id;
    private String name;
    private String host;
    private String user;
    private String status;
    private List<Address> addresses;

    @Data
    public static class Address {
        private String ip;
        private String mac;
    }

    public interface Status {
        String PAUSED = "PAUSED";
        String ACTIVE = "ACTIVE";
        String SUSPENDED = "SUSPENDED";
    }
}
