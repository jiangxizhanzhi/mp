package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Setter
@Getter
public class ProtocolExportInfo {
    private String ospfRouterId;
    private String ripLoopback;
    private List<PortProtocolExportInfo> portProtocols;

    public boolean isNotEmpty() {
        return ospfRouterId != null || ripLoopback != null || CollectionUtils.isNotEmpty(portProtocols);
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

        public boolean isNotEmpty() {
            return connectNode != null || bgpAs != null || bgpIp != null || bgpPeer != null || bgpPeerAs != null || ospfArea != null || ospfIp != null || ripIp != null || ripPear != null;
        }
    }
}
