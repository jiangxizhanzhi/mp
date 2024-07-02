package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AttachEntityUsbsData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class AttachEntityUsbsData implements CloudHttpData {

    /**
     * 虚拟机uuid
     */
    private String instanceUuid;

    /**
     * 实体usb靶标uuid
     */
    private List<String> entityUsbUuids;
}
