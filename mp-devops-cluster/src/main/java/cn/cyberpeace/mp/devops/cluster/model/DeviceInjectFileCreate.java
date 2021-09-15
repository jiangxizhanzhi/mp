package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInjectFileCreate {

    private String deviceUuid;

    private String fileUrl;

    private String fileName;

    private Long fileSize;
}
