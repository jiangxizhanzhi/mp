package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.target.TargetRoleBo;
import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.repository.RolePermissionMapper;
import cn.cyberpeace.mp.auth.repository.TargetMapper;
import cn.cyberpeace.mp.auth.service.RolePermissionModelService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.fields.RolePermissionFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@CacheConfig(cacheManager = AuthCacheConfig.CACHE_MANAGER_NORMAL)
public class RolePermissionModelServiceImpl extends BaseModelServiceImpl<RolePermissionModel, RolePermissionMapper, RolePermissionFields, QueryParams>
        implements RolePermissionModelService {

    public static final String CACHE_TARGETS_BY_ROLES = "TARGETS_BY_ROLES";

    @Autowired
    private RoleService roleService;


    @Autowired
    public RolePermissionModelServiceImpl(RolePermissionMapper modelMapper, RolePermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private TargetMapper targetMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Cacheable(value = CACHE_TARGETS_BY_ROLES)
    public List<TargetModel> listTargetsByRoleIds(List<String> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            throw new MpRuntimeException(AuthErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(AuthBasei18nCode.ROLE_ID)));
        }
        HumanCondition rolePermissionCondition = new HumanCondition()
                .filterEqual(RolePermissionModel::getTenant, AuthConstant.DEFAULT_TENANT)
                .filterIn(RolePermissionModel::getOwner, roleIds.toArray())
                .select("target");
        List<RolePermissionModel> rolePermissionModels = rolePermissionMapper.list(rolePermissionCondition);
        List<String> ids = new ArrayList<>();
        rolePermissionModels.forEach(model -> ids.add(model.getTarget()));
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        HumanCondition targetCondition = new HumanCondition()
                .filterIn(TargetModel::getId, ids.toArray())
                .filterEqual(TargetModel::getVisible, 1)
                .distinct();
        List<TargetModel> targetModels = targetMapper.list(targetCondition);
        return targetModels;
    }

    @Override
    public List<TargetRoleBo> listTargetRoleDtosByRoleIds(List<String> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            throw new MpRuntimeException(AuthErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(AuthBasei18nCode.ROLE_ID)));
        }
        HumanCondition condition = new HumanCondition()
                .link(RolePermissionModel.class)
                .link(RoleModel.class)
                .filterLink(RoleModel.class, RoleModel::getId, RolePermissionModel.class, RolePermissionModel::getOwner)
                .link(TargetModel.class)
                .filterLink(TargetModel.class, TargetModel::getId, RolePermissionModel.class, RolePermissionModel::getTarget)
                .currentLink(RoleModel.class)
                .filterIn(RoleModel::getId, roleIds.toArray())
                .selectAs(RoleModel::getId, "roleId")
                .currentLink(TargetModel.class)
                .selectAs(TargetModel::getId, "id")
                .selectAs(TargetModel::getName, "name")
                .selectAs(TargetModel::getPk, "pk")
                .selectAs(TargetModel::getParent, "parent")
                .selectAs(TargetModel::getPath, "path")
                .selectAs(TargetModel::getVisible, "visible")
                .selectAs(TargetModel::getTenant, "tenant")
                .selectAs(TargetModel::getIdentifier, "identifier")
                .selectAs(TargetModel::getType, "type")
                .selectAs(TargetModel::getCreateTime, "createTime")
                .selectAs(TargetModel::getUpdateTime, "updateTime");
        List<Map<String, Object>> maps = rolePermissionMapper.originList(condition);

        List<TargetRoleBo> targetRoles = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>(Arrays.asList(ClassUtils.getBeanClassFields(TargetRoleBo.class)));
        Set<Field> fields = getFields(new TargetRoleBo()).stream()
                .filter(field -> fieldNames.contains(field.getName()))
                .collect(Collectors.toSet());
        for (Map<String, Object> objectMap : maps) {
            TargetRoleBo targetRoleBo = new TargetRoleBo();
            setFieldValue(targetRoleBo, objectMap, fields);
            targetRoles.add(targetRoleBo);
        }
        return targetRoles;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindRoleTargets(String roleId, List<String> targetIds, boolean cleanOldTargets) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);

        var result = roleService.get(roleId);
        ResultUtils.throwExceptionIfError(result, AuthErrorCode.ROLE_NOT_EXISTED);

        if (cleanOldTargets) {
            // 清除角色绑定的权限
            boolean success = deleteByRoleId(roleId);
            if (!success) {
                throw new MpRuntimeException(AuthErrorCode.ROLE_TARGET_DELETE_ERROR);
            }
        }

        if (CollectionUtil.isEmpty(targetIds)) {
            // 完成，直接返回
            return true;
        }

        // 添加角色权限
        List<RolePermissionModel> rolePerms = new ArrayList<>();
        targetIds.forEach(tid -> {
            // todo 校验权限是否存在
            RolePermissionModel rolePerm = new RolePermissionModel();
            rolePerm.setOwner(roleId);
            rolePerm.setTarget(tid);
            rolePerm.setTenant(AuthConstant.DEFAULT_TENANT);
            rolePerms.add(rolePerm);
        });

        var re = bulkCreate(rolePerms);
        return true;
    }

    @Override
    public boolean deleteByRoleId(String roleId) {
        ParamCheck.notBlank(roleId, AuthBasei18nCode.ROLE_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RolePermissionModel::getOwner, roleId);
        getModelMapper().filterDelete(humanCondition);
        return true;
    }


    @Override
    public boolean deleteByTargetId(String targetId) {
        ParamCheck.notBlank(targetId, AuthBasei18nCode.TARGET_ID);
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(RolePermissionModel::getTarget, targetId);
        getModelMapper().filterDelete(humanCondition);
        return true;
    }

    private void setFieldValue(Object object, Map<String, Object> valueMap, Set<Field> fields) {
        for (Field field : fields) {
            try {
                PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), object.getClass());
                Method writeMethod = descriptor.getWriteMethod();
                Class<?> type = field.getType();
                Object value = valueMap.get(field.getName());
                if (value.getClass() == Timestamp.class) { // Timestamp 类型手动转成 LocalDateTime
                     value = ((Timestamp) value).toLocalDateTime();
                } else { // String 和 Integer类型直接转换
                    value = type.cast(value);
                }
                writeMethod.invoke(object, value);
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Set<Field> getFields(Object object) {
        Set<Field> fields = new HashSet<>();
        Class<?> aClass = object.getClass();
        Class<?> superclass = aClass.getSuperclass();
        while (!Objects.equals(aClass, superclass)) {
            Field[] declaredFields = aClass.getDeclaredFields();
            fields.addAll(Arrays.asList(declaredFields));
            aClass = superclass;
            if (Objects.isNull(aClass)) {
                return fields;
            }
            superclass = superclass.getSuperclass();
        }
        return fields;
    }

}
