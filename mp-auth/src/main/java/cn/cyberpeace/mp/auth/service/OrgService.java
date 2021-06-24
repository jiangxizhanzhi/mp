package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.service.BaseModelService;

import java.util.List;

public interface OrgService extends BaseModelService<OrgModel> {

    List<OrgModel> listParents();

    List<OrgModel> listByParent(String parentId);


}
