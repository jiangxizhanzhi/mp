package cn.cyberpeace.mp.cscene.service;

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
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCreateContextBO;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.core.CsceneHandler;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneRoleMapper;
import cn.cyberpeace.mp.cscene.service.param.CsceneQueryParams;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneConfigService;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Data
public class CsceneService extends BaseModelServiceImpl<CsceneModel, CsceneMapper, BaseModelFields,
        CsceneQueryParams> {

    private CsceneRoleMapper csceneRoleMapper;

    private CsceneAppConfig csceneAppConfig;

    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    private ScenePluginCloudSceneConfigService scenePluginCloudSceneConfigService;

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

    private int getActiveVirtualDeviceCount() {
        var groupFieldName = BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getVirtualType);
        Map<Object, Long> elementCountMap = scenePluginCloudSceneService
                .groupCountElement(null, null, List.of(groupFieldName))
                .getSource().get(groupFieldName);
        long activeVirtualDeviceCount = elementCountMap.getOrDefault(
                BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE, 0L) +
                elementCountMap.getOrDefault(
                        BaseCloudPluginSceneElementModel.VirtualType.CONTAINER, 0L) +
                elementCountMap.getOrDefault(
                        BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE, 0L);
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
                        BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE, 0L);
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
        var elementCount = 0;
        if (cscene == null) {
            var sceneConfigId = getSceneConfigId(csceneId);
            elementCount = getSceneConfigVirtualDeviceCount(sceneConfigId);
        } else {
            // TODO 获取需要虚拟化的节点数量
            try {
                elementCount = cscene.getSceneConfig().getTopology().getElements().size();
            } catch (Exception e) {
                log.error("Get element count error: ", e);
            }
        }

        checkSceneUsingNodeLimit(elementCount);
    }

    @Autowired
    public CsceneService(CsceneMapper modelMapper,
                         CsceneRoleMapper csceneRoleMapper,
                         CsceneAppConfig csceneAppConfig,
                         ScenePluginCloudSceneService scenePluginCloudSceneService,
                         ScenePluginCloudSceneConfigService scenePluginCloudSceneConfigService) {
        setModelMapper(modelMapper);
        setCsceneRoleMapper(csceneRoleMapper);
        setCsceneAppConfig(csceneAppConfig);
        setScenePluginCloudSceneService(scenePluginCloudSceneService);
        setScenePluginCloudSceneConfigService(scenePluginCloudSceneConfigService);
    }

    public void loadListFilterCondition(HumanCondition humanCondition, CsceneQueryParams queryParams) {
        humanCondition.filterEqual(CsceneModel::getDeleted, false);

        Integer type = queryParams.getType();
        if (type != null) {
            humanCondition.filterEqual(CsceneModel::getType, type);
        }
    }

    public ListObjServiceResult<CsceneBO> listBO(CsceneQueryParams queryParams, boolean withInstance,
                                                 CsceneContextBO context) {
        ListObjServiceResult<CsceneModel> listResult = null;
        if (StringUtils.isNotEmpty(queryParams.getSearch())) {
            // 暂时支持name的模糊搜搜
            listResult = this.listBOBySearchName(queryParams);
        } else {
            listResult = super.list(queryParams);
        }
        if (listResult.getSource() == null) {
            new ListObjServiceResult<>(null, listResult.getServiceCode(), listResult.getDetail());
        }

        var csceneObjMap = listResult.getSource().getObjs().stream()
                .collect(Collectors.toMap(BaseModel::getId, x -> x));
        var csceneRoleObjMap = readCsceneRoles(csceneObjMap.keySet()
                .toArray(new String[0])).stream().collect(Collectors.groupingBy(CsceneRoleModel::getCscene));
        List<CsceneBO> csceneBOs = new ArrayList<>();
        csceneObjMap.forEach((csceneId, csceneObj) -> {
            var roleObjs = csceneRoleObjMap.getOrDefault(csceneId, new ArrayList<>());
            var csceneHandler = new CsceneHandler(csceneObj, roleObjs, context);
            csceneBOs.add(csceneHandler.toData(withInstance));
        });
        return new ListObjServiceResult<>(new ListObjResult<>(csceneBOs, listResult.getSource().getTotal(),
                listResult.getSource().getFields()));
    }

    /**
     * 需要联合scene表进行的搜索
     *
     * @return
     */
    private ListObjServiceResult<CsceneModel> listBOBySearchName(CsceneQueryParams queryParams) {
        HumanCondition humanCondition = new HumanCondition()
                .link(CsceneModel.class, SceneConfigModel.class)
                .filterLink(CsceneModel.class, CsceneModel::getSceneConfig, SceneConfigModel.class, SceneConfigModel::getId)
                .currentLink(CsceneModel.class);
        if (queryParams.getType() != null) {
            humanCondition.filterEqual(CsceneModel::getType, queryParams.getType());
        }
        humanCondition.filterEqual(CsceneModel::getDeleted, false)
                .currentLink(SceneConfigModel.class)
                .filterContains(SceneConfigModel::getName, queryParams.getSearch())
                .pagination(queryParams.getPage(), queryParams.getPageSize());
        //排序，默认使用创建时间倒序排列
        humanCondition.setCurrentLinkName(null);
        humanCondition.orderBy("-createTime");
        var data = getModelMapper().list(humanCondition);
        var total = getModelMapper().total(humanCondition);
        return new ListObjServiceResult<>(new ListObjResult<>(data, total));
    }

    public ServiceResult<CsceneBO> getById(String id, boolean withInstance, CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(id, context);
        return new ServiceResult<>(csceneHandler.toData(withInstance));
    }

    public ServiceResult<CsceneBO> export(String id, CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(id, context);
        return new ServiceResult<>(csceneHandler.toData(false).export());
    }

    @Transactional
    public ServiceResult<CsceneBO> clone(String id, CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(id, context);
        return sync(csceneHandler.toData(false).export(), context);
    }

    @Transactional
    public ServiceResult<CsceneBO> sync(CsceneBO csceneBO, CsceneContextBO context) {
        checkSceneEditNodeLimit(csceneBO);

        var csceneHandler = new CsceneHandler(csceneBO, context);
        csceneHandler.save();
        return new ServiceResult<>(csceneHandler.toData());
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
        if (createContext.getSceneAttr() != null) {
            csceneInstance.setSceneAttr(createContext.getSceneAttr());
        }
        if (createContext.getSceneTtl() != null) {
            csceneInstance.setSceneTtl(createContext.getSceneTtl());
        }
        if (createContext.getReferenceMetaData() != null) {
            csceneInstance.setReferenceMetaData(createContext.getReferenceMetaData());
        }
        return new ServiceResult<>(csceneHandler.createTestInstance(unique, csceneInstance));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique, CsceneContextBO context) {
        checkSceneUsingNodeLimit(csceneId, null);

        var csceneHandler = new CsceneHandler(csceneId, context);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique, CsceneBO cscene,
                                                             CsceneContextBO context) {
        checkSceneUsingNodeLimit(csceneId, cscene);

        var csceneHandler = new CsceneHandler(csceneId, context);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique, cscene));
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
        if (createContext.getSceneAttr() != null) {
            csceneInstance.setSceneAttr(createContext.getSceneAttr());
        }
        if (createContext.getSceneTtl() != null) {
            csceneInstance.setSceneTtl(createContext.getSceneTtl());
        }
        if (createContext.getReferenceMetaData() != null) {
            csceneInstance.setReferenceMetaData(createContext.getReferenceMetaData());
        }
        return new ServiceResult<>(csceneHandler.createAppInstance(unique, csceneInstance));
    }

}
