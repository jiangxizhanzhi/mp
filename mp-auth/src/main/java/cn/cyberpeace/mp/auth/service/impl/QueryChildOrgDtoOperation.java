package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.service.OrgDtoOperation;
import cn.cyberpeace.mp.auth.service.OrgService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
@Scope("prototype")
public class QueryChildOrgDtoOperation implements OrgDtoOperation<OrgDto> {

    @Autowired
    private OrgService orgService;

    private List<OrgDto> orgDtoList;

    public List<OrgDto> getOrgDtoList() {
        return orgDtoList;
    }

    public void setOrgDtoList(List<OrgDto> orgDtoList) {
        this.orgDtoList = orgDtoList;
    }

    @Override
    public OrgDto doExecute(OrgDto orgDto) {




        var list = orgService.listByParent(orgDto.getId());
        var childList = new ArrayList();
        for (OrgBO orgBO : list) {
            OrgDto childDto = new OrgDto();
            BeanUtils.copyProperties(orgBO, childDto);
            childDto.execute(this);
            childList.add(childDto);
        }
        orgDto.setChildren(childList);
        if (Objects.nonNull(orgDtoList)) {
            orgDtoList.addAll(childList);
        }
        return orgDto;
    }
}
