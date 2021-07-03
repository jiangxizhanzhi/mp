package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.fields.OrgFields;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class OrgServiceImpl extends BaseModelServiceImpl<OrgModel, OrgMapper, OrgFields, QueryParams>
        implements OrgService {

    @Autowired
    private QueryParentOrgOperation queryParentOrgOperation;

    @Autowired
    public OrgServiceImpl(OrgMapper modelMapper, OrgFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public ObjServiceResult<OrgModel> create(OrgModel obj, String[] fields) {
        if (StringUtils.isNotBlank(obj.getParent())) {
            OrgModel orgModel = getModelMapper().get(obj.getParent());
            if (orgModel == null) {
                throw new IllegalArgumentException("id为"+ obj.getParent() + "的组织不存在");
            }
            String path = calculatePathByOrgId(orgModel.getId());
            path = path + AuthConstant.ORG_PATH_SEPARATOR + obj.getName();
            obj.setPath(path);
        }
        return super.create(obj, fields);
    }

    @Override
    public ObjServiceResult<OrgModel> update(OrgModel obj, String[] fields) {
        // 如果更新parent，则重新计算path
        if (StringUtils.isNotBlank(obj.getParent())) {
            OrgModel orgModel = getModelMapper().get(obj.getParent());
            if (orgModel == null) {
                throw new IllegalArgumentException("id为"+ obj.getParent() + "的组织不存在");
            }
            String path = calculatePathByOrgId(orgModel.getId());
            path = path + AuthConstant.ORG_PATH_SEPARATOR + obj.getName();
            obj.setPath(path);
        }

        return super.update(obj, fields);
    }

    @Override
    public ServiceResult<Boolean> delete(String id) {
        // todo 删除组织-权限，组织-用户关联关系
        return super.delete(id);
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
    public List<OrgDto> getTree() {
        List<OrgModel> orgs = listAll(AuthConstant.DEFAULT_TENANT);
        if (CollectionUtil.isEmpty(orgs)) {
            return new ArrayList<>();
        }
        List<OrgDto> tree = toTree(orgs);
        return tree;
    }


    @Override
    public OrgDto getHierarchicalOrgsByOrgId(String orgId) {
        Assert.notBlank(orgId, "orgId不能为空！");

        OrgModel orgModel = getModelMapper().get(orgId);
        if (orgModel == null) {
            log.info("org:{}不存在!", orgId);
            throw new IllegalArgumentException(AuthConstant.ERROR_CODE_PREFIX + "org_not_existed");
        }

        OrgDto orgDto = new OrgDto();
        BeanUtils.copyProperties(orgModel, orgDto);
        log.warn("queryParentOrgOperation:{}", queryParentOrgOperation);
        orgDto.execute(queryParentOrgOperation);

        return orgDto;
    }


    @Override
    public String calculatePathByOrgId(String orgId) {
        OrgDto orgDto = getHierarchicalOrgsByOrgId(orgId);
        Assert.notNull(orgDto, "未查询到id为" + orgId + "的组织");

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
}
