package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

/**
 * @ClassName ContainerSnapshotActionResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class ContainerSnapshotActionResult {
    private String containerId;
    private Boolean success;
    private String message;
}