package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.request.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.service.impl.TenantUserServiceImpl;
import cn.cyberpeace.mp.authbase.model.BasePermissionModel;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.*;
import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.core.CsceneHandler;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.model.CsceneAuthModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneAuthMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneRoleMapper;
import cn.cyberpeace.mp.cscene.service.filed.CsceneModelFields;
import cn.cyberpeace.mp.cscene.service.param.CsceneQueryParams;
import cn.cyberpeace.mp.cscene.service.taskpool.CsceneSnapshotDeleteTaskPool;
import cn.cyberpeace.mp.cscene.util.CsceneDataModifier;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneConfigService;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Data
public class CsceneService extends BaseModelServiceImpl<CsceneModel, CsceneMapper, BaseModelFields,
        CsceneQueryParams> {

    private CsceneRoleMapper csceneRoleMapper;

    private CsceneAppConfig csceneAppConfig;

    private CsceneInstanceMapper csceneInstanceMapper;

    private CsceneAuthMapper csceneAuthMapper;

    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    private ScenePluginCloudSceneConfigService scenePluginCloudSceneConfigService;

    private CsceneSpecialService csceneSpecialService;

    private CsceneSnapshotService csceneSnapshotService;

    private TenantUserServiceImpl tenantUserService;

    private List<CsceneRoleModel> readCsceneRoles(String[] csceneIds) {
        if (csceneIds == null || csceneIds.length == 0) {
            return new ArrayList<>();
        }
        return csceneRoleMapper.list(new HumanCondition()
                .filterIn(CsceneRoleModel::getCscene, csceneIds));
    }

    private String getSceneConfigId(String csceneId) {
        var csceneResult = get(csceneId).getSource();
        if (csceneResult == null) {
            return null;
        }
        var cscene = csceneResult.getObj();
        return cscene.getSceneConfig();
    }

    public int getActiveVirtualDeviceCount() {
        List<String> sceneIds = this.getRunningSceneIds();
        if (CollectionUtils.isEmpty(sceneIds)) {
            return 0;
        }
        var groupFieldName = BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getVirtualType);
        Map<Object, Long> elementCountMap = scenePluginCloudSceneService
                .groupCountElement(sceneIds, null, List.of(groupFieldName))
                .getSource().get(groupFieldName);
        long activeVirtualDeviceCount = elementCountMap.getOrDefault(
                BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE, 0L) +
                elementCountMap.getOrDefault(
                        BaseCloudPluginSceneElementModel.VirtualType.CONTAINER, 0L) +
                elementCountMap.getOrDefault(
                        BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE, 0L)+
                elementCountMap.getOrDefault(
                        BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS, 0L);
        return (int) activeVirtualDeviceCount;
    }

    private int getSceneConfigVirtualDeviceCount(String sceneConfigId) {
        var groupFieldName = BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getVirtualType);
        Map<Object, Long> elementCountMap = scenePluginCloudSceneConfigService
                .groupCountElement(List.of(sceneConfigId), null, List.of(groupFieldName))
                .getSource().get(groupFieldName);
        long virtualDeviceCount = elementCountMap.getOrDefault(
                BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE, 0L) +
                elementCountMap.getOrDefault(
                        BaseCloudPluginSceneElementModel.VirtualType.CONTAINER, 0L) +
                elementCountMap.getOrDefault(
                        BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE, 0L) +
                elementCountMap.getOrDefault(
                        BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS, 0L);
        return (int) virtualDeviceCount;
    }

    private void checkSceneEditNodeLimit(int count) {
        var sceneEditNodeLimit = csceneAppConfig.getSceneEditNodeLimit();
        if (count > sceneEditNodeLimit) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_EDIT_NODE_LIMIT.format(String.valueOf(sceneEditNodeLimit),
                    String.valueOf(count)));
        }
    }

    private void checkSceneEditNodeLimit(CsceneBO csceneBO) {
        var elementCount = 0;
        try {
            elementCount = csceneBO.getSceneConfig().getTopology().getElements().size();
        } catch (NullPointerException e) {
            
        } catch (Exception e) {
            log.error("Get element count error: ", e);
        }
        checkSceneEditNodeLimit(elementCount);
    }

    private void checkSceneUsingNodeLimit(int count) {
        var activeVirtualDeviceCount = getActiveVirtualDeviceCount();
        var sceneUsingNodeLimit = csceneAppConfig.getSceneUsingNodeLimit();
        if (activeVirtualDeviceCount + count > sceneUsingNodeLimit) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_USING_NODE_LIMIT.format(String.valueOf(sceneUsingNodeLimit),
                    String.valueOf(activeVirtualDeviceCount), String.valueOf(count)));
        }
    }

    private void checkSceneUsingNodeLimit(String csceneId, CsceneBO cscene) {
        if (!Boolean.TRUE.equals(csceneAppConfig.getEnableNodeLimitDbCheck())) {
            return;
        }
        var elementCount = 0;
        if (cscene == null) {
            var sceneConfigId = getSceneConfigId(csceneId);
            elementCount = getSceneConfigVirtualDeviceCount(sceneConfigId);
        } else {
            
            try {
                var pluginElements = cscene.getSceneConfig().getPlugins().get(PluginConstant.PLUGIN_KEY).getElements();
                if (pluginElements != null) {
                    for (var e : pluginElements.values()) {
                        var virtualType = e.get("virtual_type");
                        if (virtualType != null) {
                            if (Integer.parseInt(virtualType.toString()) == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER ||
                                    Integer.parseInt(virtualType.toString()) == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE ||
                                    Integer.parseInt(virtualType.toString()) == BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE ||
                                    Integer.parseInt(virtualType.toString()) == BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS
                            ) {
                                elementCount++;
                            }
                        }
                    }
                }
            } catch (NullPointerException e) {
                
            } catch (Exception e) {
                log.error("Get element count error: ", e);
            }
        }

        checkSceneUsingNodeLimit(elementCount);
    }

    @Autowired
    public CsceneService(CsceneMapper modelMapper,
                         CsceneRoleMapper csceneRoleMapper,
                         CsceneInstanceMapper csceneInstanceMapper,
                         CsceneAppConfig csceneAppConfig,
                         ScenePluginCloudSceneService scenePluginCloudSceneService,
                         ScenePluginCloudSceneConfigService scenePluginCloudSceneConfigService,
                         CsceneSpecialService csceneSpecialService,
                         CsceneSnapshotService csceneSnapshotService,
                         CsceneModelFields csceneModelFields,
                         CsceneAuthMapper csceneAuthMapper,
                         TenantUserServiceImpl tenantUserService) {
        setModelFields(csceneModelFields);
        setModelMapper(modelMapper);
        setCsceneRoleMapper(csceneRoleMapper);
        setCsceneAppConfig(csceneAppConfig);
        setScenePluginCloudSceneService(scenePluginCloudSceneService);
        setScenePluginCloudSceneConfigService(scenePluginCloudSceneConfigService);
        setCsceneInstanceMapper(csceneInstanceMapper);
        setCsceneSpecialService(csceneSpecialService);
        setCsceneSnapshotService(csceneSnapshotService);
        setCsceneAuthMapper(csceneAuthMapper);
        setTenantUserService(tenantUserService);
    }

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, CsceneQueryParams queryParams) {
        humanCondition.filterEqual(CsceneModel::getDeleted, false);

        Integer type = queryParams.getType();
        if (type != null) {
            humanCondition.filterEqual(CsceneModel::getType, type);
        }
    }

    public ServiceResult<CsceneAuthorizeBO> getCsceneAuthorizeInfo(String csceneId) {
        CsceneAuthorizeBO csceneAuthorizeBO = new CsceneAuthorizeBO();
        csceneAuthorizeBO.setAuthType(getModelMapper().get(csceneId).getAuthType());
        csceneAuthorizeBO.setSharedUsers(new ArrayList<>());
        
        if (csceneAuthorizeBO.getAuthType() == CsceneModel.AuthType.PRIVATE || csceneAuthorizeBO.getAuthType() == CsceneModel.AuthType.PUBLIC) {
            return new ServiceResult<>(csceneAuthorizeBO);
        }
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CsceneAuthModel::getTarget, csceneId);
        var csceneAuthModels = csceneAuthMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(csceneAuthModels)) {
            return new ServiceResult<>(csceneAuthorizeBO);
        }
        List<String> userIds = csceneAuthModels.stream().map(m -> m.getOwner()).collect(Collectors.toList());
        OrgUserPageRequest request = new OrgUserPageRequest();
        request.setIds(userIds.toArray(new String[0]));
        request.setPage(1);
        request.setPageSize(userIds.size());
        var users = tenantUserService.pageUsersByOrg(request);
        Map<String, UserBo> userId2User = users.getList().stream().collect(Collectors.toMap(userDto -> userDto.getId(), userDto -> userDto));
        for (String userId : userIds) {
            CsceneAuthorizeBO.SharedUser sharedUser = new CsceneAuthorizeBO.SharedUser();
            var user = userId2User.get(userId);
            sharedUser.setId(userId);
            sharedUser.setName(user.getName());
            
            sharedUser.setOrganization(user.getOrgIds().get(0));
            sharedUser.setOrganizationNameAll(StringUtils.join(user.getOrgpaths().get(0), "/"));
            sharedUser.setUsername(user.getUsername());
            csceneAuthorizeBO.getSharedUsers().add(sharedUser);
        }
        return new ServiceResult<>(csceneAuthorizeBO);
    }

    @Transactional
    public ServiceResult<Boolean> csceneAuthorize(List<String> userIds, List<String> csceneIds, Integer authType) {
        if (authType != CsceneModel.AuthType.PUBLIC && authType != CsceneModel.AuthType.SHARED) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        if (CollectionUtils.isEmpty(csceneIds)) {
            return new ServiceResult<>(true);
        }
        
        if (CollectionUtils.isEmpty(userIds) && authType != CsceneModel.AuthType.PUBLIC) {
            authType = CsceneModel.AuthType.PRIVATE;
        }
        
        deleteCsceneAuthByIds(csceneIds);
        
        List<CsceneModel> csceneModels = new ArrayList<>();
        for (String id : csceneIds) {
            CsceneModel csceneModel = new CsceneModel();
            csceneModel.setId(id);
            csceneModel.setAuthType(authType);
            csceneModels.add(csceneModel);
        }
        getModelMapper().bulkUpdate(csceneModels);
        
        if (CsceneModel.AuthType.SHARED == authType) {
            
            userIds = userIds.stream().distinct().collect(Collectors.toList());
            List<CsceneAuthModel> csceneAuthModels = new ArrayList<>();
            for (String userId : userIds) {
                for (String id : csceneIds) {
                    CsceneAuthModel csceneAuthModel = new CsceneAuthModel();
                    csceneAuthModel.setOwner(userId);
                    csceneAuthModel.setTarget(id);
                    csceneAuthModels.add(csceneAuthModel);
                }
            }
            csceneAuthMapper.bulkCreate(csceneAuthModels);
        }
        return new ServiceResult<>(true);
    }

    public ListObjServiceResult<CsceneBO> listCsceneBO(CsceneQueryParams queryParams, boolean withInstance,
                                                       CsceneContextBO context) {
        ListObjServiceResult<CsceneModel> listResult = null;
        
        if (Objects.nonNull(queryParams.getType()) && queryParams.getType() == CsceneModel.Type.TEMPLATE) {
            List<String> ids = getCsceneIds();
            if (CollectionUtils.isEmpty(ids)) {
                return new ListObjServiceResult<>(new ArrayList<>(), 0);
            }
            if (Objects.nonNull(queryParams.getIds())) {
                queryParams.setIds(Arrays.stream(queryParams.getIds())
                        .filter(s -> ids.contains(s))
                        .toArray(String[]::new));
            } else {
                queryParams.setIds(ids.toArray(new String[0]));
            }
        }
        
        if (queryParams.getOrders() == null || queryParams.getOrders().length == 0) {
            queryParams.setOrders(new String[]{"-createTime"});
        }
        
        if (queryParams.getSearch() != null && queryParams.getSearchFields() == null) {
            queryParams.setSearchFields(new String[]{"name", "referenceMetaData"});
        }
        listResult = super.list(queryParams);
        if (listResult.getSource() == null) {
            new ListObjServiceResult<>(null, listResult.getServiceCode(), listResult.getDetail());
        }

        var csceneObjMap = listResult.getSource().getObjs().stream()
                .collect(Collectors.toMap(BaseModel::getId, x -> x));
        var csceneRoleObjMap = readCsceneRoles(csceneObjMap.keySet()
                .toArray(new String[0])).stream().collect(Collectors.groupingBy(CsceneRoleModel::getCscene));
        List<CsceneBO> csceneBOs = new ArrayList<>();
        Map<String, CsceneBO> csceneBOMap = new HashMap<>();
        csceneObjMap.forEach((csceneId, csceneObj) -> {
            var roleObjs = csceneRoleObjMap.getOrDefault(csceneId, new ArrayList<>());
            var csceneHandler = new CsceneHandler(csceneObj, roleObjs, context);
            csceneBOMap.put(csceneId, csceneHandler.toData(withInstance));
        });
        
        listResult.getSource().getObjs().forEach(m -> csceneBOs.add(csceneBOMap.get(m.getId())));
        return new ListObjServiceResult<>(new ListObjResult<>(csceneBOs, listResult.getSource().getTotal(),
                listResult.getSource().getFields()));
    }

    public void setSharedUser(List<CsceneBO> csceneBOs, CsceneQueryParams queryParams) {
        if (CollectionUtils.isEmpty(csceneBOs)) {
            return;
        }
        
        if (Objects.nonNull(queryParams.getType()) && queryParams.getType() == CsceneModel.Type.TEMPLATE) {
            HumanCondition humanCondition = new HumanCondition()
                    .filterIn(CsceneAuthModel::getTarget, csceneBOs.stream().map(BaseCsceneBO::getId).toArray());
            var csceneAuthModels = csceneAuthMapper.list(humanCondition);
            Map<String, List<String>> csceneId2UserIds = new HashMap<>();
            for (var csceneAuthModel : csceneAuthModels) {
                if (!csceneId2UserIds.containsKey(csceneAuthModel.getTarget())) {
                    csceneId2UserIds.put(csceneAuthModel.getTarget(), new ArrayList<>());
                }
                csceneId2UserIds.get(csceneAuthModel.getTarget()).add(csceneAuthModel.getOwner());
            }
            csceneBOs.forEach(bo -> bo.setSharedUsers(csceneId2UserIds.getOrDefault(bo.getId(), new ArrayList<>())));
        }
    }

    public ServiceResult<CsceneBO> getById(String id, boolean withInstance, CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(id, context);
        var cscene = csceneHandler.toData(withInstance);
        cscene.setResourceOccupiedNums(NodeLimitUtils.calLimitedNodesNum(cscene));
        return new ServiceResult<>(cscene);
    }

    public ServiceResult<CsceneBO> export(String id, CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(id, context);
        return new ServiceResult<>(csceneHandler.toData(false).export());
    }

    @Transactional
    public ServiceResult<CsceneBO> clone(String id, CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(id, context);
        return sync(csceneHandler.toDataWithoutCorrectCsceneData(false).export(), context, false);
    }

    @Transactional
    public ServiceResult<CsceneBO> sync(CsceneBO csceneBO, CsceneContextBO context, boolean check) {
        if (check) {
            checkSceneEditNodeLimit(csceneBO);
        }
        if (csceneBO.getSceneConfig() == null && StringUtils.isNotEmpty(csceneBO.getId())) {
            
            var serviceResult = update(csceneBO.toModel());
            
            if (csceneBO.getEnableRecording() != null) {
                String sceneConfigId = getSceneConfigId(csceneBO.getId());
                if (sceneConfigId != null) {
                    scenePluginCloudSceneConfigService.updateEnableRecording(sceneConfigId, csceneBO.getEnableRecording());
                }
            }
            if (serviceResult != null && serviceResult.getSource() != null && serviceResult.getSource().getObj() != null) {
                CsceneBO result = new CsceneBO();
                result.fromModel(serviceResult.getSource().getObj());
                result.setEnableRecording(csceneBO.getEnableRecording());
                return new ServiceResult<>(result);
            } else {
                return new ServiceResult<>(csceneBO);
            }
        } else {
            handlerEnableRate(csceneBO);
            var csceneHandler = new CsceneHandler(csceneBO, context);
            
            String sceneName = getSceneName(csceneBO.getSceneConfig());
            csceneBO.setName(sceneName == null ? StringUtils.EMPTY : sceneName);
            csceneHandler.save();
            return new ServiceResult<>(csceneHandler.toData());
        }
    }

    private void handlerEnableRate(CsceneBO csceneBO) {
        if (csceneBO == null) {
            return;
        }
        if (StringUtils.isEmpty(csceneBO.getId())) {
            return;
        }
        var sceneConfigId = getSceneConfigId(csceneBO.getId());
        if (StringUtils.isEmpty(sceneConfigId)) {
            return;
        }
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneConfigId, null, false, null);
        CloudSceneConfigPluginHandler cloudSceneConfigPluginHandler = sceneConfigHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (cloudSceneConfigPluginHandler != null && cloudSceneConfigPluginHandler.getScene() != null) {
            if (csceneBO.getSceneConfig() == null || csceneBO.getSceneConfig().getPlugins() == null || csceneBO.getSceneConfig().getPlugins().get("cpcloud") == null) {
                return;
            }
            ScenePluginDataBO scenePluginDataBO = csceneBO.getSceneConfig().getPlugins().get("cpcloud");
            var cpcloudPluginScene = Optional.ofNullable(scenePluginDataBO.getScene())
                    .orElse(new HashMap<>());
            cpcloudPluginScene.put("enable_rate", cloudSceneConfigPluginHandler.getScene().getEnableRate());
            scenePluginDataBO.setScene(cpcloudPluginScene);
        }
    }

    private String getSceneName(SceneConfigDataBO sceneConfigDataBO) {
        if (sceneConfigDataBO == null || sceneConfigDataBO.getTopology() == null || sceneConfigDataBO.getTopology().getScene() == null || sceneConfigDataBO.getTopology().getScene().getName() == null) {
            return null;
        }
        return sceneConfigDataBO.getTopology().getScene().getName();
    }

    @Transactional
    public ServiceResult<CsceneBO> syncWithoutCheckLimit(CsceneBO csceneBO, CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(csceneBO, context);
        csceneHandler.save();
        return new ServiceResult<>(csceneHandler.toData());
    }

    public ServiceResult<Boolean> deleteById(String id, boolean logic, boolean withInstance, CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(id, context);
        var flag = csceneHandler.delete(logic, withInstance);
        if (flag) {
            
            CsceneSnapshotDeleteTaskPool.getInstance().executeTask(() -> {
                csceneSnapshotService.deleteSceneSnapshots(id);
            });
        }
        return new ServiceResult<>(flag);
    }

    public ServiceResult<Integer> bulkDeleteByIds(String[] ids, boolean logic, boolean withInstance,
                                                  CsceneContextBO context) {
        var result = 0;
        for (String id : ids) {
            if (Boolean.TRUE.equals(deleteById(id, logic, withInstance, context).getSource())) {
                result += 1;
            }
        }

        if (result < ids.length) {
            log.error("Service bulk delete cscene {} with some failed", Arrays.toString(ids));
            return new ServiceResult<>(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult<>(result);
        }
    }

    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique,
                                                              CsceneContextBO context) {
        checkSceneUsingNodeLimit(csceneId, null);

        var csceneHandler = new CsceneHandler(csceneId, context);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique));
    }

    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique, CsceneBO cscene,
                                                              CsceneContextBO context) {
        checkSceneUsingNodeLimit(csceneId, cscene);

        var csceneHandler = new CsceneHandler(csceneId, context);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique, cscene));
    }

    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique,
                                                              CsceneInstanceCreateContextBO createContext) {
        if (createContext == null) {
            return createTestInstance(csceneId, unique, new CsceneContextBO());
        }
        checkSceneUsingNodeLimit(csceneId, createContext.getCscene());

        var csceneHandler = new CsceneHandler(csceneId, createContext.getContext());
        var cscene = createContext.getCscene();
        CsceneInstanceBO csceneInstance;
        if (cscene == null) {
            csceneInstance = csceneHandler.getCscene().exportToCsceneInstance();
        } else {
            csceneInstance = cscene.exportToCsceneInstance();
        }
        if (this.need2UseSystemEnableRecording(CsceneInstanceModel.Type.APP, csceneInstance.getReferenceType())) {
            this.correctEnableRecording(csceneInstance);
        }
        if (createContext.getSceneAttr() != null) {
            csceneInstance.setSceneAttr(createContext.getSceneAttr());
        }
        if (createContext.getSceneTtl() != null) {
            csceneInstance.setSceneTtl(createContext.getSceneTtl());
        }
        if (createContext.getContext() != null && createContext.getContext().getPassThrough() != null) {
            csceneInstance.setPassThrough(createContext.getContext().getPassThrough());
        }
        if (createContext.getReferenceType() != null) {
            csceneInstance.setReferenceType(createContext.getReferenceType());
        }
        if (createContext.getReferenceMetaData() != null) {
            csceneInstance.setReferenceMetaData(createContext.getReferenceMetaData());
            CsceneDataModifier.correctName(csceneId, createContext.getReferenceMetaData(), csceneInstance, cscene, csceneHandler.getCscene(), createContext.getName());
        }
        return new ServiceResult<>(csceneHandler.createTestInstance(unique, csceneInstance));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique, CsceneContextBO context) {
        checkSceneUsingNodeLimit(csceneId, null);

        var csceneHandler = new CsceneHandler(csceneId, context);
        return new ServiceResult<>(csceneHandler.createAppInstance(unique));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique, CsceneBO cscene,
                                                             CsceneContextBO context) {
        checkSceneUsingNodeLimit(csceneId, cscene);

        var csceneHandler = new CsceneHandler(csceneId, context);
        return new ServiceResult<>(csceneHandler.createAppInstance(unique, cscene));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique,
                                                             CsceneInstanceCreateContextBO createContext) {
        if (createContext == null) {
            return createAppInstance(csceneId, unique, new CsceneContextBO());
        }
        checkSceneUsingNodeLimit(csceneId, createContext.getCscene());

        var csceneHandler = new CsceneHandler(csceneId, createContext.getContext());
        var cscene = createContext.getCscene();
        CsceneInstanceBO csceneInstance;
        if (cscene == null) {
            csceneInstance = csceneHandler.getCscene().exportToCsceneInstance();
        } else {
            csceneInstance = cscene.exportToCsceneInstance();
        }
        if (this.need2UseSystemEnableRecording(CsceneInstanceModel.Type.APP, csceneInstance.getReferenceType())) {
            this.correctEnableRecording(csceneInstance);
        }
        if (createContext.getSceneAttr() != null) {
            csceneInstance.setSceneAttr(createContext.getSceneAttr());
        }
        if (createContext.getSceneTtl() != null) {
            csceneInstance.setSceneTtl(createContext.getSceneTtl());
        }
        if (createContext.getContext() != null && createContext.getContext().getPassThrough() != null) {
            csceneInstance.setPassThrough(createContext.getContext().getPassThrough());
        }
        if (createContext.getReferenceType() != null) {
            csceneInstance.setReferenceType(createContext.getReferenceType());
        }
        if (createContext.getReferenceMetaData() != null) {
            csceneInstance.setReferenceMetaData(createContext.getReferenceMetaData());
            CsceneDataModifier.correctName(csceneId, createContext.getReferenceMetaData(), csceneInstance, cscene, csceneHandler.getCscene(), createContext.getName());
        }
        return new ServiceResult<>(csceneHandler.createAppInstance(unique, csceneInstance));
    }

    public ServiceResult<Boolean> updateElementImage(String id, String nodeId, String image) {
        String sceneConfig = this.getSceneConfigId(id);
        if (StringUtils.isEmpty(sceneConfig)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return scenePluginCloudSceneConfigService.updateElementImage(sceneConfig, nodeId, image);
    }

    private List<String> getRunningSceneIds() {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CsceneInstanceModel::getDeleted, false);
        List<CsceneInstanceModel> models = csceneInstanceMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(models)) {
            return new ArrayList<>();
        } else {
            return models.stream().map(m -> m.getScene()).collect(Collectors.toList());
        }
    }

    public List<CsceneInstanceModel> getRunningCscenes() {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CsceneInstanceModel::getDeleted, false);
        List<CsceneInstanceModel> models = csceneInstanceMapper.list(humanCondition);
        return models;
    }

    private boolean need2UseSystemEnableRecording(int testType, Integer referenceType) {
        
        if (CsceneInstanceModel.Type.TEST == testType) {
            
            return true;
        } else {
            
            if (referenceType == null) {
                return false;
            } else {
                
                return !csceneAppConfig.getHasOwnRecordingSet().contains(referenceType);
            }
        }
    }

    private void correctEnableRecording(CsceneInstanceBO csceneInstance) {
        if (csceneInstance == null) {
            return;
        }
        if (csceneInstance.getScene() != null && csceneInstance.getScene().getPlugins() != null) {
            ScenePluginDataBO scenePluginDataBO = csceneInstance.getScene().getPlugins().get("cpcloud");
            if (scenePluginDataBO != null) {
                var cpcloudPluginScene = Optional
                        .ofNullable(scenePluginDataBO.getScene())
                        .orElse(new HashMap<>());
                cpcloudPluginScene.put("enable_recording", csceneAppConfig.getSceneEnableRecording());
                scenePluginDataBO.setScene(cpcloudPluginScene);
            }
        }
    }

    private List<String> getCsceneIds() {
        var currentUser = UserCommon.getCurrentUser();
        String currentUserId = currentUser == null ? StringUtils.EMPTY : currentUser.getId();
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CsceneAuthModel::getOwner, currentUserId);
        List<String> csceneIds = csceneAuthMapper.list(humanCondition).stream()
                .map(BasePermissionModel::getTarget).collect(Collectors.toList());
        HumanCondition condition = new HumanCondition()
                .filterOrStart()
                .orEqual(CsceneModel::getAuthType, CsceneModel.AuthType.PUBLIC)
                .orAndStart()
                .andEqual(CsceneModel::getAuthType, CsceneModel.AuthType.PRIVATE)
                .andEqual(CsceneModel::getCreateUser, currentUserId)
                .orAndEnd()
                .filterOrEnd();
        csceneIds.addAll(getModelMapper().list(condition).stream()
                .map(m -> m.getId())
                .collect(Collectors.toList()));
        return csceneIds;
    }

    private void deleteCsceneAuthByIds(List<String> csceceIds) {
        HumanCondition humanCondition = new HumanCondition()
                .filterIn(CsceneAuthModel::getTarget, csceceIds.toArray());
        csceneAuthMapper.bulkDelete(csceneAuthMapper.list(humanCondition).stream()
                .map(m -> m.getId())
                .toArray(String[]::new));
    }

    public List<String> getOneSceneInstanceAllElementIds(String sceneId) {
        List<String> ids = new ArrayList<>();
        var sceneElementModels = BeanCommon.getBean(SceneElementMapper.class).list(new HumanCondition()
                .filterEqual(SceneElementModel::getScene, sceneId));
        if (CollectionUtils.isNotEmpty(sceneElementModels)) {
            ids = sceneElementModels.stream().map(SceneElementModel::getNodeId).collect(Collectors.toList());
        }
        return ids;
    }
}
