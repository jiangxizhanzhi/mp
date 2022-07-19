package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineUploadFileData implements CloudHttpData {
    private String networkId;
    private String instanceIp;
    private String username;
    private String password;
    private String file;
    private String destPath;
}
