 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneService.java.
 // File size 44130.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service;

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
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
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
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneConfigElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
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

/**
 * @ClassName CsceneService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Slf4j
@Service
@Data
public class CsceneService extends BaseModelServiceImpl<CsceneModel, CsceneMapper, BaseModelFields,
        CsceneQueryParams> {

    private CsceneRoleMapper csceneRoleMapper;

    private CsceneAppConfig csceneAppConfig;

    private CsceneInstanceMapper csceneInstanceMapper;

    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    private ScenePluginCloudSceneConfigService scenePluginCloudSceneConfigService;

    private CsceneSpecialService csceneSpecialService;

    private CsceneSnapshotService csceneSnapshotService;

    /**
     * readCsceneRoles
     *
     * @param csceneIds
     * @return
     */
    private List<CsceneRoleModel> readCsceneRoles(String[] csceneIds) {
        if (csceneIds == null || csceneIds.length == 0) {
            return new ArrayList<>();
        }
        return csceneRoleMapper.list(new HumanCondition()
                .filterIn(CsceneRoleModel::getCscene, csceneIds));
    }

    /**
     * getSceneConfigId
     *
     * @param csceneId
     * @return
     */
    private String getSceneConfigId(String csceneId) {
        ObjResult<CsceneModel> csceneResult = get(csceneId).getSource();
        if (csceneResult == null) {
            return null;
        }
        CsceneModel cscene = csceneResult.getObj();
        return cscene.getSceneConfig();
    }

    /**
     * getActiveVirtualDeviceCount
     *
     * @return int
     */
    public int getActiveVirtualDeviceCount() {
        List<String> sceneIds = this.getRunningSceneIds();
        if (CollectionUtils.isEmpty(sceneIds)) {
            return 0;
        }
        String groupFieldName = BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getVirtualType);
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

    /**
     * checkSceneEditNodeLimit
     *
     * @param count
     */
    private void checkSceneEditNodeLimit(int count) {
        int sceneEditNodeLimit = csceneAppConfig.getSceneEditNodeLimit();
        if (count > sceneEditNodeLimit) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_EDIT_NODE_LIMIT.format(String.valueOf(sceneEditNodeLimit),
                    String.valueOf(count)));
        }
    }

    /**
     * checkSceneEditNodeLimit
     *
     * @param csceneBO
     */
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

    /**
     * CsceneService
     *
     * @param modelMapper
     * @param csceneRoleMapper
     * @param csceneInstanceMapper
     * @param csceneAppConfig
     * @param scenePluginCloudSceneService
     * @param scenePluginCloudSceneConfigService
     * @param csceneSpecialService
     * @param csceneSnapshotService
     * @param csceneModelFields
     */
    @Autowired
    public CsceneService(CsceneMapper modelMapper,
                         CsceneRoleMapper csceneRoleMapper,
                         CsceneInstanceMapper csceneInstanceMapper,
                         CsceneAppConfig csceneAppConfig,
                         ScenePluginCloudSceneService scenePluginCloudSceneService,
                         ScenePluginCloudSceneConfigService scenePluginCloudSceneConfigService,
                         CsceneSpecialService csceneSpecialService,
                         CsceneSnapshotService csceneSnapshotService,
                         CsceneModelFields csceneModelFields) {
        setModelFields(csceneModelFields);
        setModelMapper(modelMapper);
        setCsceneRoleMapper(csceneRoleMapper);
        setCsceneAppConfig(csceneAppConfig);
        setScenePluginCloudSceneService(scenePluginCloudSceneService);
        setScenePluginCloudSceneConfigService(scenePluginCloudSceneConfigService);
        setCsceneInstanceMapper(csceneInstanceMapper);
        setCsceneSpecialService(csceneSpecialService);
        setCsceneSnapshotService(csceneSnapshotService);
    }

    /**
     * loadListFilterCondition
     *
     * @param humanCondition
     * @param queryParams
     */
    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, CsceneQueryParams queryParams) {
        humanCondition.filterEqual(CsceneModel::getDeleted, false);

        Integer type = queryParams.getType();
        if (type != null) {
            humanCondition.filterEqual(CsceneModel::getType, type);
        }
    }

    /**
     * listCsceneBO
     *
     * @param queryParams
     * @param withInstance
     * @param context
     * @return
     */
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
            }
        }
        return new ListObjServiceResult<>(new ListObjResult<>(csceneBOs, listResult.getSource().getTotal()));
    }

    /**
     * setSharedUser
     *
     * @param csceneBOs
     * @param queryParams
     */
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
        }
    }

    /**
     * getById
     *
     * @param id
     * @param areaId
     * @param withInstance
     * @param context
     * @return
     */
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

    /**
     * export
     *
     * @param id
     * @param context
     * @return
     */
    public ServiceResult<CsceneBO> export(String id, CsceneContextBO context) {
        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(id, context);
        return new ServiceResult<>(bcSceneConfigHandler
                                           .toData(false).export());
    }

    /**
     * clone
     *
     * @param id
     * @param context
     * @return
     */
    @Transactional
    public ServiceResult<CsceneBO> clone(String id, CsceneContextBO context) {
        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(id, context);
        var data = bcSceneConfigHandler.toDataWithoutCorrectCsceneData(false).export();
        data.setCloneFrom(id);
        return sync(data, context, false);
    }

    /**
     * sync
     *
     * @param csceneBO
     * @param context
     * @param check
     * @return
     */
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

    /**
     * handlerEnableRate
     *
     * @param csceneBO
     */
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

    /**
     * getSceneName
     *
     * @param sceneConfigDataBO
     * @return
     */
    private String getSceneName(SceneConfigDataBO sceneConfigDataBO) {
        if (sceneConfigDataBO == null || sceneConfigDataBO.getTopology() == null || sceneConfigDataBO.getTopology().getScene() == null || sceneConfigDataBO.getTopology().getScene().getName() == null) {
            return null;
        }
        return sceneConfigDataBO.getTopology().getScene().getName();
    }

    /**
     * syncForImport
     *
     * @param csceneBO
     * @param context
     * @return
     */
    @Transactional
    public ServiceResult<CsceneBO> syncForImport(CsceneBO csceneBO, CsceneContextBO context) {
        this.addCollectionPlugin(csceneBO);
        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneBO, context);
        bcSceneConfigHandler.save();
        return new ServiceResult<>(bcSceneConfigHandler.toData());
    }

    /**
     * addCollectionPlugin
     *
     * @param csceneBO
     */
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

    /**
     * deleteById
     *
     * @param id
     * @param logic
     * @param withInstance
     * @param context
     * @return
     */
    public ServiceResult<Boolean> deleteById(String id, boolean logic, boolean withInstance, CsceneContextBO context) {
        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(id, context);
        boolean flag = bcSceneConfigHandler.delete(logic, withInstance);
        if (flag) {
            // 尝试该场景模板建立的快照
            CsceneSnapshotDeleteTaskPool.getInstance().executeTask(() -> {
                csceneSnapshotService.deleteSceneSnapshots(id);
            });
        }

        CsceneModel model = new CsceneModel();
        model.setDeleted(true);
        var condition = new HumanCondition().filterEqual(CsceneModel::getCloneFrom, id);
        getModelMapper().filterUpdate(model, condition);

        return new ServiceResult<>(flag);
    }

    /**
     * bulkDeleteByIds
     *
     * @param ids
     * @param logic
     * @param withInstance
     * @param context
     * @return
     */
    public ServiceResult<Integer> bulkDeleteByIds(String[] ids, boolean logic, boolean withInstance,
                                                  CsceneContextBO context) {
        int result = 0;
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

    /**
     * createTestInstance
     *
     * @param csceneId
     * @param unique
     * @param context
     * @return
     */
    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique,
                                                              CsceneContextBO context) {

        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneId, context);
        return new ServiceResult<>(bcSceneConfigHandler.createTestInstance(unique));
    }

    /**
     * createTestInstance
     *
     * @param csceneId
     * @param unique
     * @param cscene
     * @param context
     * @return
     */
    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique, CsceneBO cscene,
                                                              CsceneContextBO context) {

        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneId, context);
        return new ServiceResult<>(bcSceneConfigHandler.createTestInstance(unique, cscene));
    }

    /**
     * createTestInstance
     *
     * @param csceneId
     * @param unique
     * @param createContext
     * @return
     */
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

    /**
     * createAppInstance
     *
     * @param csceneId
     * @param unique
     * @param context
     * @return
     */
    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique, CsceneContextBO context) {

        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneId, context);
        return new ServiceResult<>(bcSceneConfigHandler.createAppInstance(unique));
    }

    /**
     * createAppInstance
     *
     * @param csceneId
     * @param unique
     * @param cscene
     * @param context
     * @return
     */
    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique, CsceneBO cscene,
                                                             CsceneContextBO context) {

        BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneId, context);
        return new ServiceResult<>(bcSceneConfigHandler.createAppInstance(unique, cscene));
    }

    /**
     * createAppInstance
     *
     * @param csceneId
     * @param unique
     * @param createContext
     * @return
     */
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

    /**
     * updateElementImage
     *
     * @param id
     * @param nodeId
     * @param image
     * @return
     */
    public ServiceResult<Boolean> updateElementImage(String id, String nodeId, String image) {
        String sceneConfig = this.getSceneConfigId(id);
        if (StringUtils.isEmpty(sceneConfig)) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return scenePluginCloudSceneConfigService.updateElementImage(sceneConfig, nodeId, image);
    }

    /**
     * getRunningSceneIds
     *
     * @return
     */
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

    /**
     * getRunningCscenes
     *
     * @return
     */
    public List<CsceneInstanceModel> getRunningCscenes() {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CsceneInstanceModel::getDeleted, false);
        List<CsceneInstanceModel> models = csceneInstanceMapper.list(humanCondition);
        return models;
    }

    /**
     * need2UseSystemEnableRecording
     *
     * @param testType
     * @param referenceType
     * @return boolean
     */
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

    /**
     * correctEnableRecording
     *
     * @param csceneInstance
     */
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

    /**
     * getCsceneIds
     *
     * @return
     */
    private List<String> getCsceneIds() {
        List<String> csceneIds = new ArrayList<>();
        HumanCondition condition = new HumanCondition()
                .filterOrStart()
                .orEqual(CsceneModel::getAuthType, CsceneModel.AuthType.PUBLIC)
                .orAndStart()
                .andEqual(CsceneModel::getAuthType, CsceneModel.AuthType.PRIVATE)
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

    /**
     * getOneSceneInstanceAllElementIds
     *
     * @param sceneId
     * @return
     */
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

    /**
     * getOutAreaElementNum
     *
     * @param sceneId
     * @param areaId
     * @param dataBO
     * @return int
     */
    public int getOutAreaElementNum(String sceneId, String areaId, SceneConfigDataBO dataBO) {
        List<String> excludeAreaIds = new ArrayList<>();
        excludeAreaIds.add(areaId);
        if (CollectionUtils.isNotEmpty(dataBO.getTopology().getAreas())) {
            for (var a : dataBO.getTopology().getAreas()) {
                if (a.getType() == BaseSceneAreaModel.Type.COMMON) {
                    excludeAreaIds.add(a.getId());
                }
            }
        }
        var condition = new HumanCondition().filterEqual(BaseSceneElementModel::getScene, sceneId)
                .filterNotIn(BaseSceneElementModel::getArea, excludeAreaIds.toArray(new String[]{}));
        SceneConfigElementMapper sceneConfigElementMapper = BeanCommon.getBean(SceneConfigElementMapper.class);
        int num = sceneConfigElementMapper.total(condition);
        return num;
    }
}
