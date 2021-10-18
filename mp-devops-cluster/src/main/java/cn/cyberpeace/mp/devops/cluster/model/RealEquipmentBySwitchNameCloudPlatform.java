package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealEquipmentBySwitchNameCloudPlatform extends BaseCloudPlatformModel{

    private List<RealEquipmentByParams> items;
}
