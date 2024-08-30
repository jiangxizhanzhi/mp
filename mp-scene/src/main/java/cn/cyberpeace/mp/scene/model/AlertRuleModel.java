package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_alert_rule")
public class AlertRuleModel extends BaseModel {
    @DefaultStr
    private String sceneId;

    @DefaultStr
    private String name;

    @DefaultStr
    private String createUserId;

    @DefaultStr
    private String labels;

    @DefaultInt
    private Integer status;

    @DefaultStr
    private String hosts;

    @DefaultStr
    private String description;

    @DefaultStr
    private String alarmConditions;

    @DefaultStr
    private String alarmName;

    @DefaultStr
    private String alarmDescription;

    @DefaultInt
    private Integer alarmLevel;

    @DefaultStr
    private String alarmAttributes;
}
