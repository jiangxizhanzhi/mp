package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

public interface OrgService extends BaseModelService<OrgModel, QueryParams> {

    List<OrgModel> listParents();

    List<OrgModel> listByParent(String parentId);

    List<OrgModel> listAll(String tenantId);

    List<OrgDto> getTree();

    // 列出组织的层级关系
    OrgDto getHierarchicalOrgsByOrgId(String orgId);

    // 列出组织的路径
    String getPathByOrgId(String orgId);

    // 根据路径查出组织
    OrgModel getByPath(String path);


}
