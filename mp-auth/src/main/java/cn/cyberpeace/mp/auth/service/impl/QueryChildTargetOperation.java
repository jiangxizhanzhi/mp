 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name QueryChildTargetOperation.java.
 // File size 1457.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
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

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Component
@Scope("prototype")
public class QueryChildTargetOperation implements TargetOperation<TargetBo> {

    @Autowired
    @Qualifier("targetServiceImpl")
    private TargetService targetService;


    @Override
    public TargetBo doExecute(TargetBo targetBo) {
//        if (StringUtils.isBlank(targetDto.getParent())) {
//            return targetDto;
//        }

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
