package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.fields.OrgFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrgServiceImpl extends BaseModelServiceImpl<OrgModel, OrgMapper, OrgFields>
        implements OrgService {

    @Autowired
    public OrgServiceImpl(OrgMapper modelMapper, OrgFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public List<OrgModel> listParents() {
        List<OrgModel> orgs = getModelMapper().listParents();
        return orgs;
    }

    @Override
    public List<OrgModel> listByParent(String parentId) {
        Assert.notBlank(parentId, "parentId不能为空！");
        List<OrgModel> orgs = getModelMapper().listByParentId(parentId);
        return orgs;
    }
}
