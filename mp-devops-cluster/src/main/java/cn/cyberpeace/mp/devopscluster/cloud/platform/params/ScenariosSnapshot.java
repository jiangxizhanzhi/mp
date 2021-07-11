package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenariosSnapshot {

    
    private String deviceUuid;

    
    private String imageType;

    
    private String name;

    
    private String description;
}
