package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetOperation;
import cn.cyberpeace.mp.auth.service.TargetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
@Scope("prototype")
public class QueryChildTargetOperation implements TargetOperation<TargetBo> {

    @Autowired
    @Qualifier("targetServiceImpl")
    private TargetService targetService;


    @Override
    public TargetBo doExecute(TargetBo targetBo) {




        var list = targetService.listByParent(targetBo.getId());
        var childList = new ArrayList();
        for (TargetModel target : list) {
            TargetBo childDto = new TargetBo();
            BeanUtils.copyProperties(target, childDto);
            childDto.execute(this);
            childList.add(childDto);
        }
        targetBo.setChildren(childList);

        return targetBo;
    }
}
