package cn.cyberpeace.mp.scene.core.bo;

import lombok.Data;

@Data
public class ThirdPartyElementPortBO {
    private ThirdPartyElementBO thirdPartyElementBO;
    private String portId;
    private String ip;
    private String portName;

    public ThirdPartyElementPortBO(ThirdPartyElementBO thirdPartyElementBO, String ip) {
        this.thirdPartyElementBO = thirdPartyElementBO;
        this.ip = ip;
        int portIndex = thirdPartyElementBO.getPorts().size() + 1;
        this.portId = "port_" + thirdPartyElementBO.getNodeId() + "_" + portIndex;
        this.portName = "eth0/" + portIndex;
    }
}
