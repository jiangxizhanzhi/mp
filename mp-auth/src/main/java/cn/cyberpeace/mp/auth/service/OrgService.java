package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;

import java.util.List;


public interface OrgService extends BaseModelService<OrgModel, QueryParams> {

    OrgBO getOrgBO(String id);

    OrgModel getOrgModel(String id);

    List<OrgBO> listParents();

    List<OrgBO> listByParent(String parentId);

    List<OrgBO> listAll(String tenantId);

    List<OrgDto> getTree(String orgId);

    
    OrgDto getHierarchicalOrgsByOrgId(String orgId);

    
    String calculatePathByOrgId(String orgId);

    
    OrgBO getByPath(String path);

    boolean exists(String id);

    
    List<OrgDto> listAllChildIgnoreLevelExcludeSelf(String orgId);


    
    List<OrgDto> listAllParentIgnoreLevelExcludeSelf(String orgId);

    @Override
    default ObjServiceResult<OrgModel> update(OrgModel obj, String[] fields) {
        
        return BaseModelService.super.update(obj, fields);
    }

    
    String getPath(OrgModel orgModel);

    
    String getPath(OrgModel orgModel, List<OrgModel> orgModelList);

    OrgBO orgModel2OrgBO(OrgModel orgModel);

    
    String getBatchPath(List<OrgModel> orgModelList);

    
    List<List<String>> getBathPathToList(List<OrgModel> orgModelList);

}
