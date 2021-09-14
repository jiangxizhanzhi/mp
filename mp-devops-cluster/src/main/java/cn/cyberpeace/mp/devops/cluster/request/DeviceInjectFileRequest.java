package cn.cyberpeace.mp.devops.cluster.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInjectFileRequest {

    private String deviceUuid;

    private String fileUrl;

    private String fileName;

    private Long fileSize;
}
