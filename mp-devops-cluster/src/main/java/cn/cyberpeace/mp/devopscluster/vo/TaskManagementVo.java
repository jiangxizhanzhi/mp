package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.model.AssetManagementModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskManagementVo extends BaseVO{

    private AssetManagementModel assetManagementModel;

}
