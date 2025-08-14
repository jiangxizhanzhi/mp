package cn.cyberpeace.mp.cscene.model;

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
@Table("attack_command_history_model")
public class AttackCommandHistoryModel extends BaseModel {
    @DefaultStr
    private String csceneId;
    @DefaultStr
    private String category;
    @DefaultStr
    private String attackDesc;
    @DefaultStr
    private String attackTarget;
    @DefaultStr
    private String attackTargetId;
    @DefaultStr
    private String attackSourceId;
    @DefaultStr
    private String attackCommand;
    @DefaultStr
    private String attackExpectedEffect;
    @DefaultStr
    private String attackCommandOutput;
    @DefaultBool
    private Boolean needManualCheckAttackEffect;
    @DefaultBool
    private Boolean attackEffectedFlag;
    @DefaultStr(CommandStatus.RUNNING)
    private String commandStatus;


    public static class CommandStatus {
        public static final String SUCCESS = "执行成功";
        public static final String FAIL = "执行失败";
        public static final String RUNNING = "正在执行";
    }
}
