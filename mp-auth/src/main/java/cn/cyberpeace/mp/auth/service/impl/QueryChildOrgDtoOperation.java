package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgDtoOperation;
import cn.cyberpeace.mp.auth.service.OrgService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
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
//        if (StringUtils.isBlank(orgDto.getParent())) {
//            return orgDto;
//        }

        var list = orgService.listByParent(orgDto.getId());
        var childList = new ArrayList();
        for (OrgModel org : list) {
            OrgDto childDto = new OrgDto();
            BeanUtils.copyProperties(org, childDto);
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
