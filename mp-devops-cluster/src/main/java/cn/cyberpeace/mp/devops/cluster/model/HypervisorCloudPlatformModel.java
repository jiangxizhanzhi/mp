package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.Hypervisor;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HypervisorCloudPlatformModel extends BaseCloudPlatformModel {

    /**
     * 资源统计
     */
    private List<Hypervisor> hypervisors;


    
}
