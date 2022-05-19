package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.QuickCreateParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceQuickCreateCloudPlatformModel {

    /**
     * 名称
     */
    private String name;

    /**
     * 镜像名称
     */
    private String quickName;

    /**
     * 快速构建
     */
    private List<QuickCreateParam> quickCreateParams;


    public DeviceQuickCreateCloudPlatformModel(String name, String quickName) {
        this.name = name;
        this.quickName = quickName;
    }
}
