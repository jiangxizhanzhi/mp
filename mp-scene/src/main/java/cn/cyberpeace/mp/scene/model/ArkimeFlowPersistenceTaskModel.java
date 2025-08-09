package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("arkime_flow_persistence_task")
public class ArkimeFlowPersistenceTaskModel extends BaseModel {

    @DefaultStr
    private String scene;

    @DefaultStr
    private String arkimeIp;

    @DefaultBool(true)
    private Boolean valid;

    @DefaultStr
    private String latestExecuteMessage;

    @DefaultStr
    private String flowPersistenceRule;



}
