package cn.cyberpeace.mp.devopscluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DeviceInjectFileDelete
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
