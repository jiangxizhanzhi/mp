package cn.cyberpeace.mp.scene.service.impl;

import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.scene.repository.SceneConfigMapper;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SceneConfigServiceImpl extends BaseModelServiceImpl<SceneConfigModel, SceneConfigMapper, BaseModelFields>
        implements SceneConfigService {

    @Autowired
    public SceneConfigServiceImpl(SceneConfigMapper modelMapper) {
        setModelMapper(modelMapper);
    }

}
