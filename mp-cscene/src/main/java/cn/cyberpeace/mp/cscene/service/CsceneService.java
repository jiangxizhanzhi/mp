package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.core.CsceneHandler;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class CsceneService extends BaseModelServiceImpl<CsceneModel, CsceneMapper, BaseModelFields,
        QueryParams> {

    @Autowired
    public CsceneService(CsceneMapper modelMapper) {
        setModelMapper(modelMapper);
    }

    public ServiceResult<CsceneBO> getById(String id) {
        var csceneHandler = new CsceneHandler(id);
        return new ServiceResult<>(csceneHandler.toData(true));
    }

    public ServiceResult<CsceneBO> sync(CsceneBO csceneBO) {
        var csceneHandler = new CsceneHandler(csceneBO);
        csceneHandler.save();
        return new ServiceResult<>(csceneHandler.toData());
    }

    public ServiceResult<CsceneInstanceBO> createTestInstance(String csceneId, boolean unique) {
        var csceneHandler = new CsceneHandler(csceneId);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique));
    }

    public ServiceResult<CsceneInstanceBO> createAppInstance(String csceneId, boolean unique) {
        var csceneHandler = new CsceneHandler(csceneId);
        return new ServiceResult<>(csceneHandler.createTestInstance(unique));
    }

    @Override
    public ServiceResult<Boolean> delete(String id) {
        var csceneHandler = new CsceneHandler(id);
        csceneHandler.delete();
        return new ServiceResult<>(true);
    }

    @Override
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        var result = 0;
        for (String id: ids) {
            if (Boolean.TRUE.equals(delete(id).getSource())) {
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

}
