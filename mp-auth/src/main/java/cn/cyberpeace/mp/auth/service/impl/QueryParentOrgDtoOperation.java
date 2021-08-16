package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgDtoOperation;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class QueryParentOrgDtoOperation implements OrgDtoOperation<OrgDto> {

    @Autowired
    private OrgService orgService;


    @Override
    public OrgDto doExecute(OrgDto orgDto) {
        if (StringUtils.isBlank(orgDto.getParent())) {
            return orgDto;
        }

        var objSrvResult = orgService.get(orgDto.getParent());
        ObjResult<OrgModel> result = objSrvResult.getSource();
        if (result != null) {
            OrgModel parentOrgModel = result.getObj();
            if (parentOrgModel != null) {
                OrgDto parentOrgDto = new OrgDto();
                BeanUtils.copyProperties(parentOrgModel, parentOrgDto);
                orgDto.setParentOrg(parentOrgDto);
                parentOrgDto.execute(this);
            }
        }

        return orgDto;
    }
}
