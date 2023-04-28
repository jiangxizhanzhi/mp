package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @Description: 挂载实体USB对象
 * @Author: guquan
 * @Date：2023/4/27 10:16
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
