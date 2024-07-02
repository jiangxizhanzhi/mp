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

    /**
     * doExecute
     *
     * @param orgDto
     * @return
     */
    @Override
    public OrgDto doExecute(OrgDto orgDto) {
//        if (StringUtils.isBlank(orgDto.getParent())) {
//            return orgDto;
//        }

        List<OrgBO> list = orgService.listByParent(orgDto.getId());
        ArrayList childList = new ArrayList();
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
