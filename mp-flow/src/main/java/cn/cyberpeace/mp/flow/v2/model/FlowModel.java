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
    /**
     * 流量类型
     * 攻击流量：0 DDos,1 蠕虫，2 垃圾邮件，3 DNS污染，4 BGP劫持，5 缓冲区溢出，6 SQL注入，7 XSS，8 病毒
     * 普通流量：0 移动互联网，1 典型互联网，2 城市供水控制网，3 城市管网，4 电力调度网，5 轨道交通运行控制网，6 物联网，7 广播电视网，8 金融网等行业网络
     */
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
    private String fileName;
    @DefaultStr
    private String protocolConfigs;
    @DefaultStr
    private String protocolFlowUnits;
    @DefaultStr
    private String scriptType;
    @DefaultStr
    private String scriptFile;
    @DefaultStr
    private String scriptParams;
    @DefaultStr
    private String ixiaFlowName;

    public static class ResourceType {
        public static final int PCAP = 0;
        public static final int SCRIPT = 1;
        public static final int PROTOCOL_FLOW = 2;
        public static final int IXIA_FLOW = 3;

        private ResourceType() {}
    }

    public static class ScriptType {
        public static final String PYTHON = "python";
        public static final String SHELL = "shell";

        private ScriptType() {}
    }

}
