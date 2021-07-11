package cn.cyberpeace.mp.devopscluster.converter.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtNormal;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Resource;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.StartUpParameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InstallerConvertModel extends BasePageConvertModel{

    
    private String uuid;

    
    private String name;

    
    private String description;

    
    private String logo;

    
    private String categories;

    
    private Integer deployType;

    
    private ExtDocker extDocker;

    
    private List<StartUpParameter> startUpParameterList;

    
    private ExtNormal extNormal;


    
    private List<Resource> resourceList = new ArrayList<>();


    
    private Integer deviceCount;

}
