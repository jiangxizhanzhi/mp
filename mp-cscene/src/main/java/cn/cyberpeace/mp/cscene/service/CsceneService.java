package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.impl.TenantUserServiceImpl;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneAuthorizeBO;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCreateContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneRoleBO;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.core.BcSceneConfigHandler;
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
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaModel;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneAreaMapper;
import cn.cyberpeace.mp.scene.repository.SceneConfigAreaMapper;
import cn.cyberpeace.mp.scene.repository.SceneConfigElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneConfigNodeConnectionMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneNodeConnectionMapper;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneConfigService;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigModel;
import cn.cyberpeace.mp.sceneplugincollection.util.CollectionCommonConfigUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Autowired
    private UserService userService;

    private List<CsceneRoleModel> readCsceneRoles(String[] csceneIds) {
        if (csceneIds == null || csceneIds.length == 0) {
            return new ArrayList<>();
        }
        return csceneRoleMapper.list(new HumanCondition()
                .filterIn(CsceneRoleModel::getCscene, csceneIds));
    }

    private String getSceneConfigId(String csceneId) {
        ObjResult<CsceneModel> csceneResult = get(csceneId).getSource();
        if (csceneResult == null) {
            return null;
        }
        CsceneModel cscene = csceneResult.getObj();
        return cscene.getSceneConfig();
    }

    public int getActiveVirtualDeviceCount() {
        List<String> sceneIds = this.getRunningSceneIds();
        if (CollectionUtils.isEmpty(sceneIds)) {
            return 0;
        }
        SceneElementMapper sceneElementMapper = BeanCommon.getBean(SceneElementMapper.class);
        var elements = sceneElementMapper.list(new HumanCondition().filterIn(BaseSceneElementModel::getScene, sceneIds.toArray(new String[]{})));
        if (CollectionUtils.isEmpty(elements)) {
            return 0;
        }
        List<String> nodeIds = new ArrayList<>();
        elements.forEach(e -> nodeIds.add(e.getNodeId()));
        String groupFieldName = BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getVirtualType);
        Map<Object, Long> elementCountMap = scenePluginCloudSceneService
                .groupCountElement(sceneIds, nodeIds, List.of(groupFieldName))
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

    private void checkSceneEditNodeLimit(int count) {
        int sceneEditNodeLimit = csceneAppConfig.getSceneEditNodeLimit();
        if (count > sceneEditNodeLimit) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_EDIT_NODE_LIMIT.format(String.valueOf(sceneEditNodeLimit),
                    String.valueOf(count)));
        }
    }

    private void checkSceneEditNodeLimit(CsceneBO csceneBO) {
        int elementCount = 0;
        try {
            elementCount = csceneBO.getSceneConfig().getTopology().getElements().size();
        } catch (NullPointerException e) {
            // do nothing
        } catch (Exception e) {
            log.error("Get element count error: ", e);
        }
        checkSceneEditNodeLimit(elementCount);
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
        // 授权所有和私有无共享用户
        if (csceneAuthorizeBO.getAuthType() == CsceneModel.AuthType.PRIVATE || csceneAuthorizeBO.getAuthType() == CsceneModel.AuthType.PUBLIC) {
            return new ServiceResult<>(csceneAuthorizeBO);
        }
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CsceneAuthModel::getTarget, csceneId);
        List<CsceneAuthModel> csceneAuthModels = csceneAuthMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(csceneAuthModels)) {
            return new ServiceResult<>(csceneAuthorizeBO);
        }
        List<String> userIds = new ArrayList<>();
        for (CsceneAuthModel m : csceneAuthModels) {
            String owner = m.getOwner();
            userIds.add(owner);
        }
        OrgUserPageRequest request = new OrgUserPageRequest();
        request.setIds(userIds.toArray(new String[0]));
        request.setPage(1);
        request.setPageSize(userIds.size());
        PageInfo<UserBo> users = tenantUserService.pageUsersByOrg(request);
        Map<String, UserBo> userId2User = new HashMap<>();
        for (UserBo userDto : users.getList()) {
            if (userId2User.put(userDto.getId(), userDto) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        for (String userId : userIds) {
            CsceneAuthorizeBO.SharedUser sharedUser = new CsceneAuthorizeBO.SharedUser();
            UserBo user = userId2User.get(userId);
            sharedUser.setId(userId);
            sharedUser.setName(user.getName());
            // 目前用户只会有一个组织
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
        // 用户id为空且授权类型不为所有时 授权类型为私有
        if (CollectionUtils.isEmpty(userIds) && authType != CsceneModel.AuthType.PUBLIC) {
            authType = CsceneModel.AuthType.PRIVATE;
        }
        // 先删除之前的授权信息
        deleteCsceneAuthByIds(csceneIds);
        // 所有授权均需要更新场景模板授权类型
        List<CsceneModel> csceneModels = new ArrayList<>();
        for (String id : csceneIds) {
            CsceneModel csceneModel = new CsceneModel();
            csceneModel.setId(id);
            csceneModel.setAuthType(authType);
            csceneModels.add(csceneModel);
        }
        getModelMapper().bulkUpdate(csceneModels);
        // 自定义授权 每个用户生成一条授权信息
        if (CsceneModel.AuthType.SHARED == authType) {
            // 用户id去重
            List<String> list = new ArrayList<>();
            Set<String> uniqueValues = new HashSet<>();
            for (String s : userIds) {
                if (uniqueValues.add(s)) {
                    list.add(s);
                }
            }
            userIds = list;
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
        // 查询场景模板的授权信息
        if (Objects.nonNull(queryParams.getType()) && queryParams.getType() == CsceneModel.Type.TEMPLATE) {
            List<String> ids = getCsceneIds();
            if (CollectionUtils.isEmpty(ids)) {
                return new ListObjServiceResult<>(new ArrayList<>(), 0);
            }
            if (Objects.nonNull(queryParams.getIds())) {
                List<String> list = new ArrayList<>();
                for (String s : queryParams.getIds()) {
                    if (ids.contains(s)) {
                        list.add(s);
                    }
                }
                queryParams.setIds(list.toArray(new String[0]));
            } else {
                queryParams.setIds(ids.toArray(new String[0]));
            }
        }
        // 未指定排序方式时默认按创建时间倒序排列
        if (queryParams.getOrders() == null || queryParams.getOrders().length == 0) {
            queryParams.setOrders(new String[]{"-createTime"});
        }
        // 指定模糊查询但是未指定查询字段时默认查询字段为name和referenceMetaData
        if (queryParams.getSearch() != null && queryParams.getSearchFields() == null) {
            queryParams.setSearchFields(new String[]{"name", "referenceMetaData"});
        }
        listResult = super.list(queryParams);
        if (listResult.getSource() == null) {
            new ListObjServiceResult<>(null, listResult.getServiceCode(), listResult.getDetail());
        }

        Map<String,CsceneModel> csceneObjMap = new HashMap<>();
        for (CsceneModel x : listResult.getSource().getObjs()) {
            if (csceneObjMap.put(x.getId(), x) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        Map<String,List<CsceneRoleModel>> csceneRoleObjMap = new HashMap<>();
        for (CsceneRoleModel csceneRoleModel : readCsceneRoles(csceneObjMap.keySet()
                .toArray(new String[0]))) {
            csceneRoleObjMap.computeIfAbsent(csceneRoleModel.getCscene(), k -> new ArrayList<>()).add(csceneRoleModel);
        }
        List<CsceneBO> csceneBOs = new ArrayList<>();
        Map<String, CsceneBO> csceneBOMap = new HashMap<>();
        for (Map.Entry<String, CsceneModel> entry : csceneObjMap.entrySet()) {
            String csceneId = entry.getKey();
            CsceneModel csceneObj = entry.getValue();
            List<CsceneRoleModel> roleObjs = csceneRoleObjMap.getOrDefault(csceneId, new ArrayList<>());
            BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneObj, roleObjs, context);
            csceneBOMap.put(csceneId, bcSceneConfigHandler.toData(withInstance));
        }
        // 转为Map后顺序被打乱，此时再次排序
        for (CsceneModel m : listResult.getSource().getObjs()) {
            csceneBOs.add(csceneBOMap.get(m.getId()));
        }
        if (CollectionUtils.isNotEmpty(listResult.getSource().getObjs())) {
            for (CsceneBO temp : csceneBOs) {
                if (StringUtils.isNotEmpty(temp.getCreateUsername())) {
                    UserModel userModel = userService.getByUserName(temp.getCreateUsername());
                    if (userModel != null) {
                        temp.setCreateName(userModel.getName());
                    }
                }
            }
        }
        return new ListObjServiceResult<>(new ListObjResult<>(csceneBOs, listResult.getSource().getTotal()));
    }

    public void setSharedUser(List<CsceneBO> csceneBOs, CsceneQueryParams queryParams) {
        if (CollectionUtils.isEmpty(csceneBOs)) {
            return;
        }
        // 设置场景模板授权用户列表
        if (Objects.nonNull(queryParams.getType()) && queryParams.getType() == CsceneModel.Type.TEMPLATE) {
            List<String> list = new ArrayList<>();
            for (CsceneBO csceneBO : csceneBOs) {
                String id = csceneBO.getId();
                list.add(id);
            }
            HumanCondition humanCondition = new HumanCondition()
                    .filterIn(CsceneAuthModel::getTarget, list.toArray());
            List<CsceneAuthModel> csceneAuthModels = csceneAuthMapper.list(humanCondition);
            Map<String, List<String>> csceneId2UserIds = new HashMap<>();
            for (CsceneAuthModel csceneAuthModel : csceneAuthModels) {
                if (!csceneId2UserIds.containsKey(csceneAuthModel.getTarget())) {
                    csceneId2UserIds.put(csceneAuthModel.getTarget(), new ArrayList<>());
                }
                csceneId2UserIds.get(csceneAuthModel.getTarget()).add(csceneAuthModel.getOwner());
            }
            for (CsceneBO bo : csceneBOs) {
                bo.setSharedUsers(csceneId2UserIds.getOrDefault(bo.getId(), new ArrayList<>()));
            }
        }
    }

    public ServiceResult<CsceneBO> getById(String id, String areaId , boolean withInstance, CsceneContextBO context) {
        CsceneModel csceneModel = this.getModelMapper().get(id);
        if (csceneModel == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(csceneModel.getSceneConfig(), areaId, areaId == null ? false : true, null);
        SceneConfigDataBO sceneConfigData = sceneConfigHandler.toData();
        CsceneBO csceneBO = new CsceneBO();
        csceneBO.fromModel(csceneModel);
        csceneBO.setSceneConfig(sceneConfigData);
        csceneBO.setRoles(new ArrayList<>());
        List<CsceneRoleModel> roleModels = this.csceneRoleMapper.list(new HumanCondition().filterEqual(CsceneRoleModel::getCscene, id));
        if (roleModels != null) {
            for (CsceneRoleModel roleModel : roleModels) {
                CsceneRoleBO roleBO = new CsceneRoleBO();
                roleBO.fromModel(roleModel);
                csceneBO.getRoles().add(roleBO);
            }
        }
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return new ServiceResult<>(csceneBO);
    }

    public ServiceResult<CsceneBO> export(String id, CsceneContextBO context) {
        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(id, context);
        return new ServiceResult<>(bcSceneConfigHandler
                                           .toData(false).export());
    }

    @Transactional
    public ServiceResult<CsceneBO> clone(String id, CsceneContextBO context) {
        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(id, context);
        return sync(bcSceneConfigHandler
                            .toDataWithoutCorrectCsceneData(false).export(), context, false);
    }

    @Transactional
    public ServiceResult<CsceneBO> sync(CsceneBO csceneBO, CsceneContextBO context, boolean check) {
        if (check) {
            checkSceneEditNodeLimit(csceneBO);
        }
        if (csceneBO.getSceneConfig() == null && StringUtils.isNotEmpty(csceneBO.getId())) {
            // 更新name
            ObjServiceResult<CsceneModel> serviceResult = update(csceneBO.toModel());
            // 更新录屏开关
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
            if (csceneBO.getId() != null) {
                String sceneId = this.get(csceneBO.getId()).getSource().getObj().getSceneConfig();
                csceneBO.getSceneConfig().getTopology().getScene().setEid(sceneId);
            }
            handlerEnableRate(csceneBO);
            BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneBO, context);
            // 以前没有 cscene.name , 目前都和 scene.name 保持一致
            String sceneName = getSceneName(csceneBO.getSceneConfig());
            csceneBO.setName(sceneName == null ? StringUtils.EMPTY : sceneName);
            bcSceneConfigHandler.save();
            return new ServiceResult<>(bcSceneConfigHandler.toData());
        }
    }

    private void handlerEnableRate(CsceneBO csceneBO) {
        if (csceneBO == null) {
            return;
        }
        if (StringUtils.isEmpty(csceneBO.getId())) {
            return;
        }
        String sceneConfigId = getSceneConfigId(csceneBO.getId());
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
            Map<String, Object> cpcloudPluginScene = Optional.ofNullable(scenePluginDataBO.getScene())
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
    public ServiceResult<CsceneBO> syncForImport(CsceneBO csceneBO, CsceneContextBO context) {
        this.addCollectionPlugin(csceneBO);
        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneBO, context);
        bcSceneConfigHandler.save();
        return new ServiceResult<>(bcSceneConfigHandler.toData());
    }

    public void addCollectionPlugin(CsceneBO csceneBO) {
        CollectionPluginSceneConfigModel scene = new CollectionPluginSceneConfigModel();
        scene.setEnableProbeCollect(true);
        scene.setEnableFlowCollect(false);
        scene.setEnableFlowMirror(false);

        String defaultBizProbesContent = CollectionCommonConfigUtil.getConfigString(CollectionCommonConfigUtil.DEFAULT_BIZ_PROBES);
        List<Probe> defaultBizProbes = JsonCommon.snakeParse(defaultBizProbesContent, new TypeReference<List<Probe>>() {
        });
        Map<String, Map<String, Object>> pluginElements = new HashMap<>();
        for (SceneConfigElementBO e : csceneBO.getSceneConfig().getTopology().getElements()) {
            CollectionPluginSceneConfigElementBO elementModel = new CollectionPluginSceneConfigElementBO();
            elementModel.setEdited(false);
            elementModel.setEnableProbeCollect(true);
            elementModel.setProbes(defaultBizProbes);
            pluginElements.put(e.getId(), JsonCommon.beanToSnakeMap(elementModel));
        }

        ScenePluginDataBO pluginData = new ScenePluginDataBO();
        pluginData.setScene(JsonCommon.beanToSnakeMap(scene));
        pluginData.setElements(pluginElements);
        pluginData.setElementPorts(new HashMap<>());


        csceneBO.getSceneConfig().getPlugins()
                .put(cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant.PLUGIN_KEY, pluginData);
    }

    public ServiceResult<Boolean> deleteById(String id, String areaId, boolean logic, boolean withInstance, CsceneContextBO context) {
        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(id, context);
        boolean flag = bcSceneConfigHandler.delete(logic, withInstance, areaId);
        if (flag) {
            // 尝试该场景模板建立的快照
            CsceneSnapshotDeleteTaskPool.getInstance().executeTask(() -> {
                csceneSnapshotService.deleteSceneSnapshots(id);
            });
        }
        return new ServiceResult<>(flag);
    }

    public ServiceResult<Integer> bulkDeleteByIds(String[] ids, boolean logic, boolean withInstance,
                                                  CsceneContextBO context) {
        int result = 0;
        for (String id : ids) {
            if (Boolean.TRUE.equals(deleteById(id, null, logic, withInstance, context).getSource())) {
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

        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneId, context);
        return new ServiceResult<>(bcSceneConfigHandler.createTestInstance(unique));
    }

    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique, CsceneBO cscene,
                                                              CsceneContextBO context) {

        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneId, context);
        return new ServiceResult<>(bcSceneConfigHandler.createTestInstance(unique, cscene));
    }

    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique,
                                                              CsceneInstanceCreateContextBO createContext) {
        if (createContext == null) {
            return createTestInstance(csceneId, unique, new CsceneContextBO());
        }

        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneId, createContext.getContext());
        CsceneBO cscene = createContext.getCscene();
        CsceneInstanceBO csceneInstance;
        if (cscene == null) {
            csceneInstance = bcSceneConfigHandler
                    .getCscene().exportToCsceneInstance();
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
            CsceneDataModifier.correctName(csceneId, createContext.getReferenceMetaData(), csceneInstance, cscene, bcSceneConfigHandler.getCscene(), createContext.getName());
        }
        return new ServiceResult<>(bcSceneConfigHandler.createTestInstance(unique, csceneInstance));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique, CsceneContextBO context) {

        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneId, context);
        return new ServiceResult<>(bcSceneConfigHandler.createAppInstance(unique));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique, CsceneBO cscene,
                                                             CsceneContextBO context) {

        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneId, context);
        return new ServiceResult<>(bcSceneConfigHandler.createAppInstance(unique, cscene));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique,
                                                             CsceneInstanceCreateContextBO createContext) {
        if (createContext == null) {
            return createAppInstance(csceneId, unique, new CsceneContextBO());
        }

        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneId, createContext.getContext());
        CsceneBO cscene = createContext.getCscene();
        CsceneInstanceBO csceneInstance;
        if (cscene == null) {
            csceneInstance = bcSceneConfigHandler
                    .getCscene().exportToCsceneInstance();
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
            CsceneDataModifier.correctName(csceneId, createContext.getReferenceMetaData(), csceneInstance, cscene, bcSceneConfigHandler.getCscene(), createContext.getName());
        }
        return new ServiceResult<>(bcSceneConfigHandler.createAppInstance(unique, csceneInstance));
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
            List<String> list = new ArrayList<>();
            for (CsceneInstanceModel m : models) {
                String scene = m.getScene();
                list.add(scene);
            }
            return list;
        }
    }

    public List<CsceneInstanceModel> getRunningCscenes() {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CsceneInstanceModel::getDeleted, false);
        List<CsceneInstanceModel> models = csceneInstanceMapper.list(humanCondition);
        return models;
    }

    private boolean need2UseSystemEnableRecording(int testType, Integer referenceType) {
        // 使用数据库模板配置的, 根据其他字段判断是否需要使用系统的录屏开关
        if (CsceneInstanceModel.Type.TEST == testType) {
            // 后台测试启动,全部都需要用系统的录屏开关
            return true;
        } else {
            // 前台启动
            if (referenceType == null) {
                return false;
            } else {
                // 没有独立开关配置的,需要使用系统的录屏开关
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
                Map<String, Object> cpcloudPluginScene = Optional
                        .ofNullable(scenePluginDataBO.getScene())
                        .orElse(new HashMap<>());
                cpcloudPluginScene.put("enable_recording", csceneAppConfig.getSceneEnableRecording());
                scenePluginDataBO.setScene(cpcloudPluginScene);
            }
        }
    }

    private List<String> getCsceneIds() {
        UserModel currentUser = UserCommon.getCurrentUser();
        String currentUserId = currentUser == null ? StringUtils.EMPTY : currentUser.getId();
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CsceneAuthModel::getOwner, currentUserId);
        List<String> csceneIds = new ArrayList<>();
        for (CsceneAuthModel csceneAuthModel : csceneAuthMapper.list(humanCondition)) {
            String target = csceneAuthModel.getTarget();
            csceneIds.add(target);
        }
        HumanCondition condition = new HumanCondition()
                .filterOrStart()
                .orEqual(CsceneModel::getAuthType, CsceneModel.AuthType.PUBLIC)
                .orAndStart()
                .andEqual(CsceneModel::getAuthType, CsceneModel.AuthType.PRIVATE)
                .andEqual(CsceneModel::getCreateUser, currentUserId)
                .orAndEnd()
                .filterOrEnd();
        List<String> list = new ArrayList<>();
        for (CsceneModel m : getModelMapper().list(condition)) {
            String id = m.getId();
            list.add(id);
        }
        csceneIds.addAll(list);
        return csceneIds;
    }

    private void deleteCsceneAuthByIds(List<String> csceceIds) {
        HumanCondition humanCondition = new HumanCondition()
                .filterIn(CsceneAuthModel::getTarget, csceceIds.toArray());
        List<String> list = new ArrayList<>();
        for (CsceneAuthModel m : csceneAuthMapper.list(humanCondition)) {
            String id = m.getId();
            list.add(id);
        }
        csceneAuthMapper.bulkDelete(list.toArray(new String[0]));
    }

    public List<String> getOneSceneInstanceAllElementIds(String sceneId) {
        List<String> ids = new ArrayList<>();
        List<SceneElementModel> sceneElementModels = BeanCommon.getBean(SceneElementMapper.class).list(new HumanCondition()
                .filterEqual(SceneElementModel::getScene, sceneId));
        if (CollectionUtils.isNotEmpty(sceneElementModels)) {
            List<String> list = new ArrayList<>();
            for (SceneElementModel sceneElementModel : sceneElementModels) {
                String nodeId = sceneElementModel.getNodeId();
                list.add(nodeId);
            }
            ids = list;
        }
        return ids;
    }

    public boolean checkDeleteNodeConnection(String areaConnectionEid) {
        SceneConfigNodeConnectionMapper sceneNodeConnectionMapper = BeanCommon.getBean(SceneConfigNodeConnectionMapper.class);
        SceneConfigElementMapper sceneConfigElementMapper = BeanCommon.getBean(SceneConfigElementMapper.class);
        SceneConfigAreaMapper sceneConfigAreaMapper = BeanCommon.getBean(SceneConfigAreaMapper.class);

        var conn = sceneNodeConnectionMapper.get(areaConnectionEid);
        if (conn == null) {
            return true;
        }
        String scene = conn.getScene();
        List<String> nodeIds = Arrays.asList(parseNodeId(conn.getSourceNodeId()), parseNodeId(conn.getTargetNodeId()));

        var elementCondition = new HumanCondition()
                .filterEqual(BaseSceneElementModel::getScene, scene)
                .filterIn(BaseSceneElementModel::getNodeId, nodeIds.toArray(new String[]{}));
        SceneConfigElementModel elementModel = sceneConfigElementMapper.first(elementCondition);
        if (elementModel == null) {
            return true;
        }
        var areaCondition = new HumanCondition()
                .filterEqual(BaseSceneAreaModel::getScene, scene)
                .filterIn(BaseSceneAreaModel::getNodeId, nodeIds.toArray(new String[]{}));
        SceneConfigAreaModel areaModel = sceneConfigAreaMapper.first(areaCondition);
        if (areaModel == null) {
            return true;
        }

        // 和层内节点相连 则不允许删除
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(scene, areaModel.getNodeId(), null);
        var connectedElements = sceneConfigHandler.getElementConnectedElements(elementModel.getNodeId());
        if (CollectionUtils.isNotEmpty(connectedElements)) {
            return false;
        }

        // 和层内区域相连 则不允许删除
        var areaNodeConnections = new ArrayList<>(sceneConfigHandler.getAreaNodeConnectionMap().values());
        for (var areaNodeConnection : areaNodeConnections) {
            String source = areaNodeConnection.getSourceNodeId();
            String target = areaNodeConnection.getTargetNodeId();
            if (StringUtils.equals(source, elementModel.getNodeId()) || StringUtils.equals(target, elementModel.getNodeId())) {
                return false;
            }
        }
        return true;
    }

    private String parseNodeId(String input){
        String regex = "port_(.+)_layer_[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String extracted = matcher.group(1);
            return extracted;
        }
        return input;
    }
}
