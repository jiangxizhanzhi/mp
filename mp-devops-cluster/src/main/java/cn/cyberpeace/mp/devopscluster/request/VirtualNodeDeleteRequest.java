package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.cloud.platform.params.InstanceDeleteData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualNodeDeleteRequest {
    private List<InstanceDeleteData> instances;
}