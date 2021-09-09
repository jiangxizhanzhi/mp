package cn.cyberpeace.mp.devops.cluster.model;


import cn.cyberpeace.mp.devops.cluster.model.common.AccessProtocolsPort;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessProtocolsPortCloudPlatform extends BaseCloudPlatformModel{

    private List<AccessProtocolsPort> items;
}
