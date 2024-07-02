package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

/**
 * @ClassName VirtualMachineModifyData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class VirtualMachineModifyData implements CloudHttpData {
    private String name;
    private Integer memory;
    private Integer cpu;
    private Integer diskSize;
    private String displayType;
    private String diskId;
}
