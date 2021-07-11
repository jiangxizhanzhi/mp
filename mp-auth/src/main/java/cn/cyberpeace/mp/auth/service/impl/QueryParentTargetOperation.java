package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetOperation;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class QueryParentTargetOperation implements TargetOperation<TargetBo> {

    @Autowired
    @Qualifier("targetServiceImpl")
    private TargetService targetService;


    @Override
    public TargetBo doExecute(TargetBo targetBo) {
        if (StringUtils.isBlank(targetBo.getParent())) {
            return targetBo;
        }

        var objSrvResult = targetService.get(targetBo.getParent());
        ObjResult<TargetModel> result = objSrvResult.getSource();
        if (result != null) {
            TargetModel parentTarget = result.getObj();
            if (parentTarget != null) {
                TargetBo parentTargetBo = new TargetBo();
                BeanUtils.copyProperties(parentTarget, parentTargetBo);
                targetBo.setParentTarget(parentTargetBo);
                parentTargetBo.execute(this);
            }
        }

        return targetBo;
    }
}
