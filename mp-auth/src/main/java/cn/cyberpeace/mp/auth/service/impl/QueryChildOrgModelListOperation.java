package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgDtoOperation;
import cn.cyberpeace.mp.auth.service.OrgModelOperation;
import cn.cyberpeace.mp.auth.service.OrgService;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
@Data
public class QueryChildOrgModelListOperation implements OrgModelOperation<OrgModel> {

    @Autowired
    private OrgService orgService;

    private List<OrgModel> orgList = new ArrayList<>();


    @Override
    public OrgModel doExecute(OrgModel orgModel) {
        var list = orgService.listByParent(orgModel.getId());
        orgList.addAll(list);
        return null;
    }


}
