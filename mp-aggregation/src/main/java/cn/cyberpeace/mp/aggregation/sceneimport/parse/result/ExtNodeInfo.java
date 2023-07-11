package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Setter
@Getter
public class ExtNodeInfo {
    private String ospfRouterId;
    private String ripLoopback;
    private List<PortProtocolExportInfo> portProtocols;
    private String formatVersion = "v1";

    public boolean isNotEmpty() {
        if (StringUtils.isNotEmpty(ospfRouterId) || StringUtils.isNotEmpty(ripLoopback)) {
            return true;
        }
        int notEmptyPortProtocolNum = 0;
        if (CollectionUtils.isNotEmpty(portProtocols)) {
            for (var portProtocol : portProtocols) {
                if (portProtocol.isNotEmpty()) {
                    notEmptyPortProtocolNum++;
                }
            }
        }
        return notEmptyPortProtocolNum > 0;
    }

    @Setter
    @Getter
    public static class PortProtocolExportInfo {
        private String connectNode;
        private String bgpAs;
        private String bgpIp;
        private String bgpPeer;
        private String bgpPeerAs;
        private String ospfArea;
        private String ospfIp;
        private String ripIp;
        private String ripPear;
        private String displayContext;
        private String vertices;
        private Integer egress;
        private Integer ingress;
        private Integer delay;
        private Integer loss;
        private String connectMode;
        private String type;

        public boolean isNotEmpty() {
            return StringUtils.isNotEmpty(bgpAs)
                    || StringUtils.isNotEmpty(bgpIp)
                    || StringUtils.isNotEmpty(bgpPeer)
                    || StringUtils.isNotEmpty(bgpPeerAs)
                    || StringUtils.isNotEmpty(ospfArea)
                    || StringUtils.isNotEmpty(ospfIp)
                    || StringUtils.isNotEmpty(ripIp)
                    || StringUtils.isNotEmpty(ripPear)
                    || StringUtils.isNotEmpty(displayContext)
                    || StringUtils.isNotEmpty(vertices)
                    || StringUtils.isNotEmpty(connectMode)
                    || egress != null
                    || ingress != null
                    || loss != null
                    || delay != null
                    || StringUtils.isNotEmpty(type);
        }
    }
}
