package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.TargetDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetOperation;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class QueryParentTargetOperation implements TargetOperation<TargetDto> {

    @Autowired
    private TargetService targetService;


    @Override
    public TargetDto doExecute(TargetDto targetDto) {
        if (StringUtils.isBlank(targetDto.getParent())) {
            return targetDto;
        }

        var objSrvResult = targetService.get(targetDto.getParent());
        if (objSrvResult != null) {
            ObjResult<TargetModel> result = objSrvResult.getSource();
            TargetModel parentTarget = result.getObj();
            if (parentTarget != null) {
                TargetDto parentTargetDto = new TargetDto();
                BeanUtils.copyProperties(parentTarget, parentTargetDto);
                targetDto.setParentTarget(parentTargetDto);
                parentTargetDto.execute(this);
            }
        }

        return targetDto;
    }
}
