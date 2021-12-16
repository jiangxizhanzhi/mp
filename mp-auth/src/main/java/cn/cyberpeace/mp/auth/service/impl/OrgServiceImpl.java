package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.fields.OrgFields;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private UpdateChildOrgDtoPathDtoOperation updateChildOrgPathOperation;

    @Autowired
    private OrgPermissionService orgPermissionService;

    @Autowired
    private OrgUserService orgUserService;

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
            String path = calculatePathByOrgId(parentOrg.getId());
            path = path + AuthConstant.ORG_PATH_SEPARATOR + obj.getName();

            if (Objects.nonNull(getByPath(path))) {
                log.info("org child already exited! {}", path);
                throw new MpRuntimeException(AuthErrorCode.ORG_CHILD_ALREADY_EXISTED);
            }

            obj.setPath(path);
        }
        return super.create(obj, fields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ObjServiceResult<OrgModel> update(OrgModel obj, String[] fields) {

        OrgModel oldOrg = getModelMapper().get(obj.getId());
        if (Objects.isNull(oldOrg)) {
            log.error("org {} not existed!", obj.getId());
            throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED.format(obj.getId()));
        }
        // 不允许直接更新path
        obj.setPath(null);

        if (StringUtils.isBlank(obj.getIdentifier())) {
            obj.setIdentifier(null);
        }

        // 如果更新parent，则重新计算path
        if (StringUtils.isNotBlank(obj.getParent())) {
            OrgModel parentOrg = getModelMapper().get(obj.getParent());
            if (Objects.isNull(parentOrg)) {
                log.error("parent org {} not existed!", obj.getParent());
                throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED.format(obj.getParent()));
            }
            String path = calculatePathByOrgId(parentOrg.getId());
            path = path + AuthConstant.ORG_PATH_SEPARATOR + obj.getName();
            obj.setPath(path);
        }

        // 是否需要更新组织名称
        boolean needUpdateName = StringUtils.isNotBlank(obj.getName()) && !Objects.equals(obj.getName(), oldOrg.getName());

        if (needUpdateName) {
            // 更新自身的path
            String newPath = "";
            if (StringUtils.isNotBlank(oldOrg.getParent())) {
                newPath = calculatePathByOrgId(oldOrg.getParent()) + AuthConstant.ORG_PATH_SEPARATOR + obj.getName();
            } else {
                newPath = obj.getName();
            }
            obj.setPath(newPath);
        }

        // 查看组织path是否重复
        if (StringUtils.isNotBlank(obj.getPath())) {
            String path = obj.getPath();
            if (Objects.nonNull(getByPath(path))) {
                throw new MpRuntimeException(AuthErrorCode.ORG_CHILD_ALREADY_EXISTED);
            }
        }

        // 1.先更新OrgModel信息
        ObjServiceResult<OrgModel> result = super.update(obj, fields);
        OrgModel orgUpdated = result.getSource().getObj();

        // 2.再更新orgpath信息，这样可以查询到前面未提交sql更新的内容
        if (needUpdateName) {
            // 如果更新组织名称，则递归更新所有下级组织的path
            OrgDto orgDto = new OrgDto();
            BeanUtils.copyProperties(orgUpdated, orgDto);
            updateChildOrgPathOperation.doExecute(orgDto);
        }

        return result;
    }

    @Override
    public ServiceResult<Integer> bulkUpdate(List<OrgModel> objs) {
//        return super.bulkUpdate(objs);
        throw new MpRuntimeException(AuthErrorCode.OP_ERROR);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Boolean> delete(String orgId) {
        Assert.notBlank(orgId, "orgId不能为空！");
        List<OrgModel> children = listByParent(orgId);
        if (CollectionUtil.isNotEmpty(children)) {
            throw new MpRuntimeException(AuthErrorCode.ORG_CANNOT_DELETE_WITH_CHILD_EXISTED);
        }

        deleteReleated(orgId);

        return super.delete(orgId);
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
        return super.bulkDelete(ids);
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

    @Override
    public List<OrgModel> listAll(String tenantId) {
        List<OrgModel> orgs = getModelMapper().listAll(tenantId);
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
            log.info("org:{}不存在!", parentOrgId);
            throw new MpRuntimeException(AuthErrorCode.ORG_NOT_EXISTED);
        }

        OrgDto orgDto = new OrgDto();
        BeanUtils.copyProperties(parentOrg, orgDto);
        log.warn("queryChildOrgOperation:{}", queryChildOrgDtoOperation);
        orgDto.execute(queryChildOrgDtoOperation);

        return CollectionUtil.toList(orgDto);
    }


    @Override
    public OrgDto getHierarchicalOrgsByOrgId(String orgId) {
        Assert.notBlank(orgId, "orgId不能为空！");

        OrgModel orgModel = getModelMapper().get(orgId);
        if (orgModel == null) {
            log.info("org:{}不存在!", orgId);
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
        Assert.notBlank(path, "path不能为空！");
        OrgModel orgModel = getModelMapper().getByPath(path);
        return orgModel;
    }

    @Override
    public boolean exists(String id) {
        Assert.notBlank(id, "id不能为空！");
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
        Assert.notEmpty(orgs, "orgs should not be empty!");
        Map<String, List<OrgModel>> map = new HashMap<>();
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
                    //                    current.(parent);
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
        List<UserModel> users = orgUserService.listUsersByOrgId(orgId);
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
