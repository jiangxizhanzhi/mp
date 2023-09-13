package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhanzhi
 */
@Data
@Accessors(chain = true)
public class MitreEventModel {

    private String agentName;

    private String documentId;

    private String techniqueId;

    private String technique;

    private List<String> tacticList;

    private String ruleDescription;

    /**
     * 发生时间
     */
    private LocalDateTime time;

    public static MitreEventModel getMitreEventModelFromWazuhFileRoot(WazuhFileRoot wazuhFileRoot) {
        MitreEventModel mitreEventModel = new MitreEventModel().setAgentName(wazuhFileRoot.getAgent().getName())
                .setTime(TimeUtils.dateToLocalDateTime(wazuhFileRoot.getTimestamp())).setDocumentId(wazuhFileRoot.getId())
                .setRuleDescription(wazuhFileRoot.getRule().getDescription());
        List<String> techniqueList = wazuhFileRoot.getRule().getMitre().getTechnique();
        List<String> techniqueIdList = wazuhFileRoot.getRule().getMitre().getId();
        if (!CollectionUtils.isEmpty(techniqueList) && !CollectionUtils.isEmpty(techniqueIdList)) {
            mitreEventModel.setTechnique(techniqueList.get(0)).setTechniqueId(techniqueIdList.get(0)).setTacticList(wazuhFileRoot.getRule()
                    .getMitre().getTactic());
        }
        return mitreEventModel;
    }

}

