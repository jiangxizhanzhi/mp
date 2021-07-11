package cn.cyberpeace.mp.devopscluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallerFormDataCloudPlatformModel implements Serializable {

    
    private String uuid;

    
    private String name;

    
    private String description;

    
    private String logo;

    
    private String categories;

    
    private Integer deployType;

    
    private String settings;

    
    private String resources;

    
    private String tmpdirUuid;

    
    private String userFilename;
}
