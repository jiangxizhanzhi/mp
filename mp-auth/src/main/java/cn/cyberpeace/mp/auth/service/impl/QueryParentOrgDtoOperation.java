 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name QueryParentOrgDtoOperation.java.
 // File size 1876.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgDtoOperation;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @ClassName QueryParentOrgDtoOperation
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see OrgDtoOperation
 */
@Component
@Scope("prototype")
public class QueryParentOrgDtoOperation implements OrgDtoOperation<OrgDto> {

    @Autowired
    private OrgService orgService;


    /**
     * doExecute
     *
     * @param orgDto
     * @return
     */
    @Override
    public OrgDto doExecute(OrgDto orgDto) {
        if (StringUtils.isBlank(orgDto.getParent())) {
            return orgDto;
        }

        ObjServiceResult<OrgModel> objSrvResult = orgService.get(orgDto.getParent());
        ObjResult<OrgModel> result = objSrvResult.getSource();
        if (result != null) {
            OrgModel parentOrgModel = result.getObj();
            OrgBO parentOrgBO = orgService.orgModel2OrgBO(parentOrgModel);
            if (parentOrgBO != null) {
                OrgDto parentOrgDto = new OrgDto();
                BeanUtils.copyProperties(parentOrgBO, parentOrgDto);
                orgDto.setParentOrg(parentOrgDto);
                parentOrgDto.execute(this);
            }
        }

        return orgDto;
    }
}
