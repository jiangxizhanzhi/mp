package cn.cyberpeace.mp.devopscluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInjectFileDelete {

    /**
     * 镜像ID
     */
    private String deviceUuid;

    /**
     * 文件uuid
     */
    private String fileUuid;
}
