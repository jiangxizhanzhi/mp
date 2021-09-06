package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 设备信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceCloudPlatformModel extends BaseCloudPlatformModel {

    /**
     * 数据列表
     */
    private List<Device> items;
}
