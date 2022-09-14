package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base;

import cn.cyberpeace.mp.sceneplugingns3.util.gns3http.Gns3HttpData;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public abstract class BaseNodeCreateData implements Gns3HttpData {
    private String name;
    private String computeId;
    private String nodeType;
    private String symbol;
    private BaseProperties properties;

    public BaseNodeCreateData(String computeId, String nodeType, String symbol) {
        this.computeId = computeId;
        this.nodeType = nodeType;
        this.symbol = symbol;
    }

    @Setter
    @Getter
    public static class Properties {
        private List<Port> portsMapping;
        private String image;
        private String platform;
        private Integer ram;
        private Integer dynamipsId;
    }

    @Setter
    @Getter
    public static class Port {
        private String ethertype;
        private String name;
        private Integer portNumber;
        private String type;
        private Integer vlan;
        @JSONField(name = "interface")
        private String Interface;
    }
}
