package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioDeleteDeviceParams {

    /**
     * UUID
     */
    private String deviceUuid;

    /**
     * 类型 vm/container
     */
    private String imageType;
}
