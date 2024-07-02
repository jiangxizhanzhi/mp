package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName VirtualMachineUploadFileData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see VirtualMachinePrivateOperateData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineUploadFileData extends VirtualMachinePrivateOperateData {
    private String file;
    private String destPath;
}
