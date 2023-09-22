package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.Data;

/**
 * @ClassName WarningRuleConfigRequest
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/22 10:04
 **/
@Data
public class WarningRuleConfigRequest extends QueryParams {

    private String warningClass;


}
