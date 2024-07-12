 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name QueryParentTargetOperation.java.
 // File size 1921.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetOperation;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @ClassName QueryParentTargetOperation
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see TargetOperation
 */
@Component
@Scope("prototype")
public class QueryParentTargetOperation implements TargetOperation<TargetBo> {

    @Autowired
    @Qualifier("targetServiceImpl")
    private TargetService targetService;


    /**
     * doExecute
     *
     * @param targetBo
     * @return
     */
    @Override
    public TargetBo doExecute(TargetBo targetBo) {
        if (StringUtils.isBlank(targetBo.getParent())) {
            return targetBo;
        }

        ObjServiceResult<TargetModel> objSrvResult = targetService.get(targetBo.getParent());
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
