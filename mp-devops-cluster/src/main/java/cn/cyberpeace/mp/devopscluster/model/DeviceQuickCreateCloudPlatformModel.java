package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.QuickCreateParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceQuickCreateCloudPlatformModel {

    
    private String name;

    
    private String quickName;

    
    private List<QuickCreateParam> quickCreateParams;


    public DeviceQuickCreateCloudPlatformModel(String name, String quickName) {
        this.name = name;
        this.quickName = quickName;
    }
}
