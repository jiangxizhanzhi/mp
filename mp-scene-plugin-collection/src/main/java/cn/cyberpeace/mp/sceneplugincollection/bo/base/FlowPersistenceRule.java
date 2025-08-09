package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class FlowPersistenceRule {
    private String ip;
    private String mac;
    private String port;
    private String protocol;
    private String vlan;


    public String convertToQueryString() {
        StringBuilder query = new StringBuilder("expression=");
        if (ip != null) {
            query.append("ip=" + ip + "&&");
        }
        if (mac != null) {
            query.append("mac=" + mac + "&&");
        }
        if (port != null) {
            query.append("port=" + port + "&&");
        }
        if (protocol != null) {
            query.append("protocols=" + protocol + "&&");
        }
        if (vlan != null) {
            query.append("vlan=" + vlan + "&&");
        }
        return StringUtils.stripEnd(query.toString(), "&&");

    }
}
