package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.service.OrgDtoOperation;
import cn.cyberpeace.mp.auth.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class UpdateChildOrgDtoPathDtoOperation implements OrgDtoOperation<OrgDto> {

    @Autowired
    private OrgService orgService;

    @Autowired
    private OrgMapper orgMapper;


    @Override
    public OrgDto doExecute(OrgDto orgDto) {












        return orgDto;
    }
}
