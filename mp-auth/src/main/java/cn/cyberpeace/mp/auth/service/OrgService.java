package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface OrgService extends BaseModelService<OrgModel, QueryParams> {

    List<OrgModel> listParents();

    List<OrgModel> listByParent(String parentId);

    List<OrgModel> listAll(String tenantId);

    List<OrgDto> getTree(String orgId);

    // 列出组织的层级关系
    OrgDto getHierarchicalOrgsByOrgId(String orgId);

    // 列出组织的路径
    String calculatePathByOrgId(String orgId);

    // 根据路径查出组织
    OrgModel getByPath(String path);

    boolean exists(String id);

    /**
     * 列出所有的子组织，忽略层级限制
     * 注意！不包括传入的org本身
     * @param orgId
     */
    List<OrgDto> listAllChildIgnoreLevelExcludeSelf(String orgId);


    /**
     * 列出所有的父组织，忽略层级限制
     * 注意！不包括传入的org本身
     * @param orgId
     */
    List<OrgDto> listAllParentIgnoreLevelExcludeSelf(String orgId);

    @Override
    default ObjServiceResult<OrgModel> update(OrgModel obj, String[] fields) {
        // todo 如果是更新组织名称，那要更新关联的组织orgpath
        return BaseModelService.super.update(obj, fields);
    }
}
