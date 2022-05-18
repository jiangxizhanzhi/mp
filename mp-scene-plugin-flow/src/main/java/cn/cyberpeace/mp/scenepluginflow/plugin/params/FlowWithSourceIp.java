package cn.cyberpeace.mp.scenepluginflow.plugin.params;

import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import lombok.Data;

@Data
public class FlowWithSourceIp {
    private Flow flow;
    private String sourceIp;
}
