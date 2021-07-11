package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;


@Data
public class ResetCplcoudAllIpsData implements CloudHttpData {
    private Controller controller;
    private List<Compute> compute;
    private Office office;

    @Data
    public static class Controller {
        private String ip;
        private String netmask;
        private String gateway;
        private String allocationPools;
    }

    @Data
    public static class Compute {
        private String name;
        private boolean status;
        private String ip;
    }

    @Data
    public static class Office {
        private String ip;
    }
}
