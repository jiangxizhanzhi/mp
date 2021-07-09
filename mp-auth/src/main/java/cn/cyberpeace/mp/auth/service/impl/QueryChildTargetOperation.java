package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.target.TargetDto;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetOperation;
import cn.cyberpeace.mp.auth.service.TargetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope("prototype")
public class QueryChildTargetOperation implements TargetOperation<TargetDto> {

    @Autowired
    private TargetService targetService;


    @Override
    public TargetDto doExecute(TargetDto targetDto) {
//        if (StringUtils.isBlank(targetDto.getParent())) {
//            return targetDto;
//        }

        var list = targetService.listByParent(targetDto.getId());
        var childList = new ArrayList();
        for (TargetModel target : list) {
            TargetDto childDto = new TargetDto();
            BeanUtils.copyProperties(target, childDto);
            childDto.execute(this);
            childList.add(childDto);
        }
        targetDto.setChildren(childList);

        return targetDto;
    }
}
