package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CsceneService extends BaseModelServiceImpl<CsceneModel, CsceneMapper, BaseModelFields,
        QueryParams> {

    private SceneConfigService sceneConfigService;

    @Autowired
    public CsceneService(CsceneMapper modelMapper, SceneConfigService sceneConfigService) {
        setModelMapper(modelMapper);
        this.sceneConfigService = sceneConfigService;
    }

    public ServiceResult<CsceneBO> create(CsceneBO csceneBO) {
        return new ServiceResult<>(csceneBO);
    }

    public ServiceResult<CsceneBO> update(CsceneBO csceneBO) {
        return new ServiceResult<>(csceneBO);
    }

    public ServiceResult<Boolean> delete(String id) {
        return new ServiceResult<>(true);
    }

}
