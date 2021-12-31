package cn.cyberpeace.mp.devops.cluster.model;


import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.AccessProtocolsPort;
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
