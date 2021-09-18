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
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCreateContext;
import cn.cyberpeace.mp.cscene.bo.CsceneStatBO;
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

    public ListObjServiceResult<CsceneBO> listBO(QueryParams queryParams, boolean withInstance) {
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
            var csceneHandler = new CsceneHandler(csceneObj, roleObjs);
            csceneBOs.add(csceneHandler.toData(withInstance));
        });
        return new ListObjServiceResult<>(new ListObjResult<>(csceneBOs, listResult.getSource().getTotal(),
                listResult.getSource().getFields()));
    }

    public ServiceResult<CsceneBO> getById(String id, boolean withInstance) {
        var csceneHandler = new CsceneHandler(id);
        return new ServiceResult<>(csceneHandler.toData(withInstance));
    }

    public ServiceResult<CsceneBO> export(String id) {
        var csceneHandler = new CsceneHandler(id);
        return new ServiceResult<>(csceneHandler.toData(false).export());
    }

    @Transactional
    public ServiceResult<CsceneBO> sync(CsceneBO csceneBO) {
        var csceneHandler = new CsceneHandler(csceneBO);
        csceneHandler.save();
        return new ServiceResult<>(csceneHandler.toData());
    }

    public ServiceResult<Boolean> deleteById(String id, boolean withInstance) {
        var csceneHandler = new CsceneHandler(id);
        csceneHandler.delete(withInstance);
        return new ServiceResult<>(true);
    }

    public ServiceResult<Integer> bulkDeleteByIds(String[] ids, boolean withInstance) {
        var result = 0;
        for (String id: ids) {
            if (Boolean.TRUE.equals(deleteById(id, withInstance).getSource())) {
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

    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique) {
        var csceneHandler = new CsceneHandler(csceneId);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique));
    }

    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique, CsceneBO cscene) {
        var csceneHandler = new CsceneHandler(csceneId);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique, cscene));
    }

    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique,
                                                              CsceneInstanceCreateContext createContext) {
        if (createContext == null) {
            return createTestInstance(csceneId, unique);
        }

        var csceneHandler = new CsceneHandler(csceneId);
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

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique) {
        var csceneHandler = new CsceneHandler(csceneId);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique, CsceneBO cscene) {
        var csceneHandler = new CsceneHandler(csceneId);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique, cscene));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique,
                                                             CsceneInstanceCreateContext createContext) {
        if (createContext == null) {
            return createAppInstance(csceneId, unique);
        }

        var csceneHandler = new CsceneHandler(csceneId);
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

    public ServiceResult<List<CsceneStatBO>> stat(String[] ids) {
        // TODO
        return new ServiceResult<>(null);
    }
}
