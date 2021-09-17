package cn.cyberpeace.mp.devops.cluster.model;

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
     * 服务ID
     */
    private List<String> installerUuids;
}
