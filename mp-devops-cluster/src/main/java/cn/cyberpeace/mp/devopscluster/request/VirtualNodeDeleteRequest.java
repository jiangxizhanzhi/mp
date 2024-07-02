package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.cloud.platform.params.InstanceDeleteData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName VirtualNodeDeleteRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class VirtualNodeDeleteRequest {
    private List<InstanceDeleteData> instances;
}