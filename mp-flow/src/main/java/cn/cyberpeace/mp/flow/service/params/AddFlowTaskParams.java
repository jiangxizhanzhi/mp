package cn.cyberpeace.mp.flow.service.params;

import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddFlowTaskParams {
    List<AddFlowTaskFlowInfo> tasks;
    String actuatorId;
    String networkId;
    String targetIp;
}
