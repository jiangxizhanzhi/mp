package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.org.OrgDto;
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

    /**
     * getOrgBO
     *
     * @param id
     * @return
     */
    OrgBO getOrgBO(String id);

    /**
     * getOrgModel
     *
     * @param id
     * @return
     */
    OrgModel getOrgModel(String id);

    /**
     * listParents
     *
     * @return
     */
    List<OrgBO> listParents();

    /**
     * listByParent
     *
     * @param parentId
     * @return
     */
    List<OrgBO> listByParent(String parentId);

    /**
     * listAll
     *
     * @param tenantId
     * @return
     */
    List<OrgBO> listAll(String tenantId);

    /**
     * getTree
     *
     * @param orgId
     * @return
     */
    List<OrgDto> getTree(String orgId);

    /**
     * 列出组织的层级关系
     *
     * @param orgId
     * @return
     */
    OrgDto getHierarchicalOrgsByOrgId(String orgId);

    // 根据路径查出组织
    OrgBO getByPath(String path);

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

    /**
     * 获取组织完整path
     * @param orgModel
     * @return
     */
    String getPath(OrgModel orgModel);

    /**
     * 获取组织完整path
     * @param orgModel
     * @param orgModelList 对应租户下的所有部门列表
     * @return
     */
    String getPath(OrgModel orgModel, List<OrgModel> orgModelList);

    OrgBO orgModel2OrgBO(OrgModel orgModel);

    /**
     * 批量获取组织完整path
     * @param orgModelList
     * @return
     */
    String getBatchPath(List<OrgModel> orgModelList);

    /**
     * 批量获取组织路径list
     * @param orgModelList
     * @return
     */
    List<List<String>> getBathPathToList(List<OrgModel> orgModelList);

}
