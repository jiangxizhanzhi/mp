package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.service.OrgDtoOperation;
import cn.cyberpeace.mp.auth.service.OrgService;
import org.springframework.beans.BeanUtils;
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
        var childlist = orgService.listByParent(orgDto.getId());
        for (OrgModel child : childlist) {
            OrgDto childDto = new OrgDto();
            BeanUtils.copyProperties(child, childDto);
            String newPath = orgService.calculatePathByOrgId(child.getId());

            // 直接通过mapper更新，不用orgService更新
            orgMapper.updatePath(child.getId(), newPath);

            childDto.execute(this);
        }

        return orgDto;
    }
}
