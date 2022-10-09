package cn.cyberpeace.mp.flow.v2.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_flow")
public class FlowModel extends BaseModel {
    private String name;
    @DefaultStr
    private String description;
    @DefaultInt
    private Integer category;
    @DefaultInt
    private Integer interactiveType;
    @DefaultInt(ResourceType.PCAP)
    private Integer resourceType;
    @DefaultStr
    private String protocols;
    @DefaultStr
    private String pcapFile;
    @DefaultStr
    private String protocolConfigs;
    @DefaultStr
    private String protocolFlowUnits;
    @DefaultStr(ScriptType.PYTHON)
    private String scriptType;
    @DefaultStr
    private String scriptFile;
    @DefaultStr
    private String scriptParams;
    @DefaultStr
    private String ixiaFLowName;

    public static class ResourceType {
        public static final Integer PCAP = 0;
        public static final Integer SCRIPT = 1;
        public static final Integer PROTOCOL_FLOW = 2;
        public static final Integer IXIA_FLOW = 3;

        private ResourceType() {}
    }

    public static class ScriptType {
        public static final String PYTHON = "python";
        public static final String SHELL = "shell";

        private ScriptType() {}
    }

}
