package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

@Data
public class ContainerSnapshotActionResult {
    private String containerId;
    private Boolean success;
    private String message;
}