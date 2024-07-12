 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UpdateChildOrgDtoPathDtoOperation.java.
 // File size 1485.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.service.OrgDtoOperation;
import cn.cyberpeace.mp.auth.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @ClassName UpdateChildOrgDtoPathDtoOperation
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see OrgDtoOperation
 */
@Component
@Scope("prototype")
public class UpdateChildOrgDtoPathDtoOperation implements OrgDtoOperation<OrgDto> {

    @Autowired
    private OrgService orgService;

    @Autowired
    private OrgMapper orgMapper;


    /**
     * doExecute
     *
     * @param orgDto
     * @return
     */
    @Override
    public OrgDto doExecute(OrgDto orgDto) {
//        var childlist = orgService.listByParent(orgDto.getId());
//        for (OrgModel child : childlist) {
//            OrgDto childDto = new OrgDto();
//            BeanUtils.copyProperties(child, childDto);
//            String newPath = orgService.calculatePathByOrgId(child.getId());
//
//            // 直接通过mapper更新，不用orgService更新
//            orgMapper.updatePath(child.getId(), newPath);
//
//            childDto.execute(this);
//        }

        return orgDto;
    }
}
