package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.org.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserCacheService;
import cn.cyberpeace.mp.auth.service.fields.OrgFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class OrgServiceImpl extends BaseModelServiceImpl<OrgModel, OrgMapper, OrgFields, QueryParams>
        implements OrgService {

    @Autowired
    private QueryParentOrgDtoOperation queryParentOrgDtoOperation;

    @Autowired
    private QueryChildOrgDtoOperation queryChildOrgDtoOperation;

    @Autowired
    private OrgPermissionService orgPermissionService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private OrgPathService orgPathService;

    @Autowired
    public OrgServiceImpl(OrgMapper modelMapper, OrgFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public ObjServiceResult<OrgModel> create(OrgModel obj, String[] fields) {
        if (StringUtils.isNotBlank(obj.getParent())) {
            OrgModel parentOrg = getModelMapper().get(obj.getParent());
            if (Objects.isNull(parentOrg)) {
                log.error("org {} not existed!", obj.getParent());
                throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED.format(obj.getParent()));
            }

            // 校验组织是否重复
            if (this.checkExist(obj)) {
                log.info("org child already exited! {}", obj.getName());
                throw new MpRuntimeException(AuthErrorCode.ORG_CHILD_ALREADY_EXISTED);
            }
        }

        ObjServiceResult<OrgModel> orgModelObjServiceResult = super.create(obj, fields);
        OrgModel createModel = orgModelObjServiceResult.getData();
        createModel.setPath(this.getPath(createModel));
        return orgModelObjServiceResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ObjServiceResult<OrgModel> update(OrgModel obj, String[] fields) {

        OrgModel oldOrg = getModelMapper().get(obj.getId());
        if (Objects.isNull(oldOrg)) {
            log.error("org {} not existed!", obj.getId());
            throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED.format(obj.getId()));
        }

        if (StringUtils.isBlank(obj.getIdentifier())) {
            obj.setIdentifier(null);
        }

        // 是否需要更新组织名称
        boolean needUpdateName = StringUtils.isNotBlank(obj.getName()) && !Objects.equals(obj.getName(), oldOrg.getName());

        // 查看组织path是否重复
        if (needUpdateName) {
            if (this.checkExist(obj)) {
                log.info("org child already exited! {}", obj.getName());
                throw new MpRuntimeException(AuthErrorCode.ORG_CHILD_ALREADY_EXISTED);
            }
        }

        // 1.先更新OrgModel信息
        ObjServiceResult<OrgModel> result = super.update(obj, fields);
        OrgModel resultModel = result.getData();
        resultModel.setPath(this.getPath(resultModel));

        userCacheService.evictAllUserCache();
        return result;
    }

    /**
     * 校验组织是否重复
     * @param obj
     * @return
     */
    private boolean checkExist(OrgModel obj) {
        String parentId = obj.getParent();
        String name = obj.getName();
        if (Objects.isNull(parentId) || Objects.isNull(name)) {
            log.error("parentId and name can not be empty");
            return true;
        }

        List<OrgModel> orgModelList = this.getModelMapper().listByParentIdAndName(parentId, name);
        if (CollectionUtil.isNotEmpty(orgModelList)) {
            if (StringUtils.isNotBlank(obj.getId())) {
                // 判断是否当前组织
                for (OrgModel orgModel : orgModelList) {
                    if (orgModel.getId().equals(obj.getId())) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public String getPath(OrgModel orgModel) {
        String path = orgModel.getName();
        if (StringUtils.isNotBlank(orgModel.getParent())) {

            List<OrgModel> orgModelList = orgPathService.listByTenant(orgModel.getTenant());

            while (StringUtils.isNotBlank(orgModel.getParent())) {
                String parentId = orgModel.getParent();
                Optional<OrgModel> orgModelOptional =
                        orgModelList.stream().filter(org -> org.getId().equals(parentId)).findFirst();
                if (orgModelOptional.isPresent()) {
                    orgModel = orgModelOptional.get();
                    path = orgModel.getName().concat(AuthConstant.ORG_PATH_SEPARATOR).concat(path);
                } else {
                    orgModel = new OrgModel();
                }
            }
        }

        return path;
    }

    @Override
    public String getBatchPath(List<OrgModel> orgModelList) {
        String allOrgPath = StringUtils.EMPTY;
        if (CollectionUtil.isNotEmpty(orgModelList)) {
            for (OrgModel orgModel : orgModelList) {
                allOrgPath = allOrgPath.concat(AuthConstant.COMMA).concat(this.getPath(orgModel));
            }
            allOrgPath = allOrgPath.substring(1);
        }

        return allOrgPath;
    }

    @Override
    public List<List<String>> getBathPathToList(List<OrgModel> orgModelList) {
        List<List<String>> resultList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(orgModelList)) {
            for (OrgModel orgModel : orgModelList) {
                List<String> pathList = new ArrayList<>();
                pathList.add(orgModel.getName());
                if (StringUtils.isNotBlank(orgModel.getParent())) {
                    List<OrgModel> allOrgModelList = this.getModelMapper().listAll(orgModel.getTenant());

                    while (StringUtils.isNotBlank(orgModel.getParent())) {
                        String parentId = orgModel.getParent();
                        Optional<OrgModel> orgModelOptional =
                                allOrgModelList.stream().filter(org -> org.getId().equals(parentId)).findFirst();
                        if (orgModelOptional.isPresent()) {
                            orgModel = orgModelOptional.get();
                            pathList.add(0, orgModel.getName());
                        } else {
                            orgModel = new OrgModel();
                        }
                    }
                }
                resultList.add(pathList);
            }
        }

        return resultList;
    }

    @Override
    public ServiceResult<Integer> bulkUpdate(List<OrgModel> objs) {
//        return super.bulkUpdate(objs);
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean> delete(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);
        List<OrgModel> children = listByParent(orgId);
        if (CollectionUtil.isNotEmpty(children)) {
            throw new MpRuntimeException(AuthErrorCode.ORG_CANNOT_DELETE_WITH_CHILD_EXISTED);
        }

        deleteReleated(orgId);

        var ret = super.delete(orgId);
        userCacheService.evictAllUserCache();
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        for (String id : ids) {
            List<OrgModel> children = listByParent(id);
            if (CollectionUtil.isNotEmpty(children)) {
                throw new MpRuntimeException(AuthErrorCode.ORG_CANNOT_DELETE_WITH_CHILD_EXISTED);
            }

            deleteReleated(id);
        }
        var ret = super.bulkDelete(ids);
        userCacheService.evictAllUserCache();
        return ret;
    }

    @Override
    public List<OrgModel> listParents() {
        List<OrgModel> orgs = getModelMapper().listParents();
        orgs.forEach(org -> org.setPath(this.getPath(org)));
        return orgs;
    }

    @Override
    public List<OrgModel> listByParent(String parentId) {
        ParamCheck.notBlank(parentId, AuthBasei18nCode.ORG_PARENT);
        List<OrgModel> orgs = getModelMapper().listByParentId(parentId);
        orgs.forEach(org -> org.setPath(this.getPath(org)));
        return orgs;
    }

    @Override
    public List<OrgModel> listAll(String tenantId) {
        List<OrgModel> orgs = getModelMapper().listAll(tenantId);
        orgs.forEach(org -> org.setPath(this.getPath(org)));
        return orgs;
    }

    @Override
    public List<OrgDto> getTree(String parentOrgId) {
        if (StringUtils.isBlank(parentOrgId)) {
            // 查询所有组织树
            List<OrgModel> orgs = listAll(AuthConstant.DEFAULT_TENANT);
            if (CollectionUtil.isEmpty(orgs)) {
                return new ArrayList<>();
            }
            List<OrgDto> tree = toTree(orgs);

            sortTree(tree);

            return tree;
        }

        // 根据parentId查询单个组织树
        OrgModel parentOrg = getModelMapper().get(parentOrgId);
        if (parentOrg == null) {
            log.info("org:{} not existed!", parentOrgId);
            throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED);
        }

        OrgDto orgDto = new OrgDto();
        BeanUtils.copyProperties(parentOrg, orgDto);
        orgDto.execute(queryChildOrgDtoOperation);

        return CollectionUtil.toList(orgDto);
    }


    @Override
    public OrgDto getHierarchicalOrgsByOrgId(String orgId) {
        ParamCheck.notBlank(orgId, AuthBasei18nCode.ORG_ID);

        OrgModel orgModel = getModelMapper().get(orgId);
        if (orgModel == null) {
            log.info("org:{} not existed!", orgId);
            throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED);
        }

        OrgDto orgDto = new OrgDto();
        BeanUtils.copyProperties(orgModel, orgDto);
        log.warn("queryParentOrgDtoOperation:{}", queryParentOrgDtoOperation);
        orgDto.execute(queryParentOrgDtoOperation);

        return orgDto;
    }


    @Override
    public String calculatePathByOrgId(String orgId) {
        OrgDto orgDto = getHierarchicalOrgsByOrgId(orgId);

        if (Objects.isNull(orgDto)) {
            log.error("org {} not existed!", orgId);
            throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED.format(orgId));
        }

        StringBuilder path = new StringBuilder(orgDto.getName());
        while (true) {
            if ((orgDto = orgDto.getParentOrg()) != null) {
                path.insert(0, orgDto.getName() + AuthConstant.ORG_PATH_SEPARATOR);
            } else {
                break;
            }
        }

        return path.toString();
    }


    @Override
    public OrgModel getByPath(String path) {
        ParamCheck.notBlank(path, AuthBasei18nCode.ORG_PATH);
        OrgModel orgModel = null;
        if (StringUtils.isNotBlank(path)) {
            String[] pathArray = path.split(AuthConstant.ORG_PATH_SEPARATOR);
            String parentId = StringUtils.EMPTY;
            for (int i = 0; i < pathArray.length; i++) {
                String p = pathArray[i].trim();
                OrgModel tmpModel;
                List<OrgModel> orgModelList = this.getModelMapper().listByParentIdAndName(parentId, p);
                if (CollectionUtils.isNotEmpty(orgModelList)) {
                    tmpModel = orgModelList.get(0);
                    parentId = tmpModel.getId();

                    // 最后一个，获取组织
                    if (i == (pathArray.length - 1)) {
                        orgModel = tmpModel;
                        orgModel.setPath(path);
                    }
                }
            }
        }
        return orgModel;
    }

    @Override
    public boolean exists(String id) {
        ParamCheck.notBlank(id, AuthBasei18nCode.ORG_ID);

        OrgModel orgModel = getModelMapper().get(id);
        return Objects.nonNull(orgModel);
    }


    /**
     * 列出所有的子组织，忽略层级限制
     * 注意！不包括传入的org本身
     * @param orgId
     */
    @Override
    public List<OrgDto> listAllChildIgnoreLevelExcludeSelf(String orgId) {
        // 展示所有层级的子组织，不包括自己
        log.info("show all child orgs ignore level, orgId:{}", orgId);
        ObjServiceResult<OrgModel> result = get(orgId);
        if (Objects.nonNull(result.getSource())) {
            OrgModel parentOrg = result.getSource().getObj();
            OrgDto parentOrgDto = new OrgDto();
            BeanUtils.copyProperties(parentOrg, parentOrgDto);
            queryChildOrgDtoOperation.setOrgDtoList(new ArrayList<>());
            parentOrgDto.execute(queryChildOrgDtoOperation);

            List<OrgDto> orgDtos = queryChildOrgDtoOperation.getOrgDtoList();

            return orgDtos;
        }

        return new ArrayList<>();
    }


    @Override
    public List<OrgDto> listAllParentIgnoreLevelExcludeSelf(String orgId) {
        // 展示所有层级的父组织，不包括自己
        log.info("show all parent orgs ignore level, orgId:{}", orgId);
        OrgDto orgDto = getHierarchicalOrgsByOrgId(orgId);

        if (Objects.nonNull(orgDto)) {
            List<OrgDto> parentOrgDtos = new ArrayList<>();
            while (true) {
                if ((orgDto = orgDto.getParentOrg()) != null) {
                    parentOrgDtos.add(orgDto);
                } else {
                    break;
                }
            }
            return parentOrgDtos;
        }

        return new ArrayList<>();
    }

    private List<OrgDto> toTree(List<OrgModel> orgs) {
        ParamCheck.notEmpty(orgs, AuthBasei18nCode.ORG_LIST);
        Map<String, OrgDto> mapTmp = new HashMap<>();

        //Save all nodes to a map
        for (OrgModel orgModel : orgs) {
            OrgDto targetDto = new OrgDto();
            BeanUtils.copyProperties(orgModel, targetDto);
            mapTmp.put(targetDto.getId(), targetDto);
        }

        //loop and assign parent/child relationships
        for (Map.Entry<String, OrgDto> entry : mapTmp.entrySet()) {
            OrgDto current = entry.getValue();
            String parentId = current.getParent();

            if (StringUtils.isNotBlank(parentId)) {
                OrgDto parent = mapTmp.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(current);
                    mapTmp.put(parentId, parent);
                }
            }

        }

        List<OrgDto> parentOrgs = new ArrayList<>();
        //get the root
        for (OrgDto node : mapTmp.values()) {
            if(StringUtils.isBlank(node.getParent())) {
                parentOrgs.add(node);
            }
        }

        return parentOrgs;
    }


    /**
     * 删除组织-权限，组织-人员的关系
     * @param orgId
     */
    private void deleteReleated(String orgId) {
        if (StringUtils.isBlank(orgId)) {
            return;
        }

        // 如果组织下有人员，不允许删除
        List<UserModel> users = orgUserService.listUsersByOrgIdNoRestrict(orgId);
        if (CollectionUtil.isNotEmpty(users)) {
            throw new MpRuntimeException(AuthErrorCode.ORG_CANNOT_DELETE_WITH_USER_EXISTED);
        }

        boolean deleteOrgPermissionSuccess = orgPermissionService.deleteByOrgId(orgId);
        boolean deleteOrgUserSuccess = orgUserService.deleteByOrgId(orgId);

        if(!deleteOrgPermissionSuccess || !deleteOrgUserSuccess) {
            log.info("deleteOrgPermissionSuccess:{}, deleteOrgUserSuccess:{}", deleteOrgPermissionSuccess, deleteOrgUserSuccess);
            throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
        }
    }

    // 组织树排序
    private void sortTree(List<OrgDto> orgs) {
        orgs.forEach(o -> {
            if (CollectionUtil.isNotEmpty(o.getChildren())) {
                sortTree(o.getChildren());
            }
        });

        orgs.sort((o1, o2) -> o1.getPk() - o2.getPk());
    }
}
