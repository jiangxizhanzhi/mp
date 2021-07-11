package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Installer extends BaseCloudPlatformResult {

    
    private Integer id;

    
    private String uuid;

    
    private String name;

    
    private String description;

    
    private String logo;

    
    private String categories;

    
    private Integer deployType;

    
    private ExtDocker extDocker;

    
    private ExtNormal extNormal;

    
    private List<Resource> resourceList;

    
    private Integer deviceCount;
}
