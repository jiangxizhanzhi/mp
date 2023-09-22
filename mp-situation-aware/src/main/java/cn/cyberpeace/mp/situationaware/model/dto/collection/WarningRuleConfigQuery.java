package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.situationaware.constant.BaseKeyAndName;
import cn.cyberpeace.mp.situationaware.constant.SuricataConstant;
import cn.cyberpeace.mp.situationaware.constant.WarningTargetEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Optional;

/**
 * @ClassName WarningRuleConfigQuery
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/22 11:20
 **/
@Data
public class WarningRuleConfigQuery extends QueryParams {
    private String warningTarget;

    public static WarningRuleConfigParams toParams(WarningRuleConfigQuery warningRuleConfigQuery) {
        WarningRuleConfigParams warningRuleConfigParams = new WarningRuleConfigParams();
        BeanUtils.copyProperties(warningRuleConfigQuery, warningRuleConfigParams);
        String warningTarget = warningRuleConfigQuery.getWarningTarget();
        if (StringUtils.isNotBlank(warningTarget)) {
            warningRuleConfigParams.setWarningTarget(Optional.ofNullable(
                    BaseKeyAndName.getByName(warningTarget, WarningTargetEnum.class)).map(WarningTargetEnum::getKey).orElse(SuricataConstant.ZERO));

        }
        return warningRuleConfigParams;
    }
}
