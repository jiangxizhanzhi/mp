package cn.cyberpeace.mp.devops.cluster.cloud.platform.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenariosSnapshot {

    /**
     * UUID
     */
    private String deviceUuid;

    /**
     * 类型 vm/container
     */
    private String imageType;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;
}
