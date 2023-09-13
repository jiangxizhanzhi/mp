package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.situationaware.model.MitreEventModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Mitre attack 标准事件记录
 * @author zhanzhi
 */
@Data
@Accessors(chain = true)
public class MitreRuleBo {

    private String eid;

    private String technique;

    private String techniqueId;

    private List<String> tacticList;

    private String ruleDescription;


    public static MitreRuleBo getFromMitreEventModel(MitreEventModel mitreEventModel) {
        return new MitreRuleBo().setEid(mitreEventModel.getAgentName())
                .setTechnique(mitreEventModel.getTechnique())
                .setTechniqueId(mitreEventModel.getTechniqueId())
                .setTacticList(mitreEventModel.getTacticList())
                .setRuleDescription(mitreEventModel.getRuleDescription());
    }

}
