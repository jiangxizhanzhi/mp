package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.*;
import cn.cyberpeace.mp.cscene.core.CsceneHandler;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CsceneService extends BaseModelServiceImpl<CsceneModel, CsceneMapper, BaseModelFields,
        QueryParams> {

    @Autowired
    private CsceneRoleMapper csceneRoleMapper;

    private List<CsceneRoleModel> readCsceneRoles(String[] csceneIds) {
        if (csceneIds == null || csceneIds.length == 0) {
            return new ArrayList<>();
        }
        return csceneRoleMapper.list(new HumanCondition()
                .filterIn(CsceneRoleModel::getCscene, csceneIds));
    }

    @Autowired
    public CsceneService(CsceneMapper modelMapper) {
        setModelMapper(modelMapper);
    }

    public ListObjServiceResult<CsceneBO> listBO(QueryParams queryParams, boolean withInstance,
                                                 CsceneContextBO context) {
        var listResult = super.list(queryParams);
        if (listResult.getSource() == null) {
            new ListObjServiceResult<>(null, listResult.getServiceCode(), listResult.getDetail());
        }

        var csceneObjMap = listResult.getSource().getList().stream()
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
        var csceneHandler = new CsceneHandler(csceneBO, context);
        csceneHandler.save();
        return new ServiceResult<>(csceneHandler.toData());
    }

    public ServiceResult<Boolean> deleteById(String id, boolean withInstance, CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(id, context);
        csceneHandler.delete(withInstance);
        return new ServiceResult<>(true);
    }

    public ServiceResult<Integer> bulkDeleteByIds(String[] ids, boolean withInstance, CsceneContextBO context) {
        var result = 0;
        for (String id: ids) {
            if (Boolean.TRUE.equals(deleteById(id, withInstance, context).getSource())) {
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
        var csceneHandler = new CsceneHandler(csceneId, context);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique));
    }

    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique, CsceneBO cscene,
                                                              CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(csceneId, context);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique, cscene));
    }

    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique,
                                                              CsceneInstanceCreateContextBO createContext) {
        if (createContext == null) {
            return createTestInstance(csceneId, unique, new CsceneContextBO());
        }

        var csceneHandler = new CsceneHandler(csceneId, createContext.toCsceneContext());
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
        return new ServiceResult<>(csceneHandler.createTestInstance(unique, csceneInstance));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique, CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(csceneId, context);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique, CsceneBO cscene,
                                                             CsceneContextBO context) {
        var csceneHandler = new CsceneHandler(csceneId, context);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique, cscene));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique,
                                                             CsceneInstanceCreateContextBO createContext) {
        if (createContext == null) {
            return createAppInstance(csceneId, unique, new CsceneContextBO());
        }

        var csceneHandler = new CsceneHandler(csceneId, createContext.toCsceneContext());
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
        
        return new ServiceResult<>(csceneHandler.createAppInstance(unique, csceneInstance));
    }

    public ServiceResult<List<CsceneStatBO>> stat(String[] ids, CsceneContextBO context) {
        // TODO
        return new ServiceResult<>(null);
    }
}
