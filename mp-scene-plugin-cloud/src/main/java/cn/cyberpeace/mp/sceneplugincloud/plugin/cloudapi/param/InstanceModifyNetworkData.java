package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

@Data
public class InstanceModifyNetworkData implements CloudHttpData {
    private String nodeId;
    private Boolean special;
    private List<AddNic> addNics;
    private List<DeleteNic> deleteNics;
    private String hwVifModel;
    private String type;

    @Data
    public static class AddNic {
        private String networkId;
        private String vnicName;
        private String fixedIp;
        private String cidr;
        private String gateway;
        private String macAddress;
        private VirtualNic.QosRule qosRule;
    }

    @Data
    public static class DeleteNic {
        private String interfaceId;
    }
}
