package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetCalculate;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudImageAddDTO extends BaseDTO {

    // 云镜像类型
    private String cloudTargetType;

    // 共同表
    private DeviceCloudTargetPO commonCloudTarget;

    // 计算表
    private DeviceCloudTargetCalculate calculateTarget;
}
