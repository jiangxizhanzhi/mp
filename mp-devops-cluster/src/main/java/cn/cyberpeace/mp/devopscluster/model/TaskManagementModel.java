package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table("mp_task_management")
public class TaskManagementModel extends BaseModel {

    //任务名称
    private String name;
    //资产id
    private String assetsId;
    //资产id
    private String assetsName;
    //红方人员或者队伍id
    private String redId;
    //裁判id
    private String refereeId;
    //相关联的场景ID
    private String sceneId;

}
