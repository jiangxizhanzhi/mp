package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.StartUpParameter;
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
    private List<QuickCreateParams> quickCreateParams;

    @Data
    @NoArgsConstructor
    public static class QuickCreateParams{

        private String installerUuid;

        private List<StartUpParameter> settings;
    }

    public DeviceQuickCreateCloudPlatformModel(String name, String quickName) {
        this.name = name;
        this.quickName = quickName;
    }
}
