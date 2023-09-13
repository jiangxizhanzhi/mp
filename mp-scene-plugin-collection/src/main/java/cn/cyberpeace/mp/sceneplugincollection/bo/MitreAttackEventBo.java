package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.bo.BaseEventBo;
import cn.cyberpeace.mp.sceneplugincollection.bo.MitreRuleBo;
import cn.cyberpeace.mp.sceneplugincollection.constant.HostEventEnum;
import cn.cyberpeace.mp.situationaware.model.MitreEventModel;
import cn.cyberpeace.mp.situationaware.model.wazuh.rule.Rule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 *  MITRE ATT&CK 定义的主机攻击技术业务对象
 * @author zhanzhi
 */
@Data
@Accessors(chain = true)
@ApiModel("mitre att 事件业务对象")
public class MitreAttackEventBo {

    @ApiModelProperty("事件发生的element节点对象")
    private SceneElementBO sceneElementBO;

    @ApiModelProperty("事件发生的类型枚举对象")
    private HostEventEnum hostEventEnum;

    @ApiModelProperty("事件发生匹配的mitre 规则对象")
    private MitreRuleBo mitreRuleBo;

    @ApiModelProperty("基础事件信息对象")
    private BaseEventBo baseEventBo;

    public static MitreAttackEventBo getFromModel(MitreEventModel mitreEventModel, Map<String, SceneElementBO> elementMap) {
        MitreRuleBo mitreRuleBoFromModel = MitreRuleBo.getFromMitreEventModel(mitreEventModel);
        BaseEventBo baseEventBoFromModel = BaseEventBo.getFromMitreEventModel(mitreEventModel);
        String eid = mitreRuleBoFromModel.getEid();


        return new MitreAttackEventBo()
                .setSceneElementBO(elementMap.get(eid)).setHostEventEnum(HostEventEnum.FILE).setMitreRuleBo(mitreRuleBoFromModel).setBaseEventBo(baseEventBoFromModel);
    }

}
