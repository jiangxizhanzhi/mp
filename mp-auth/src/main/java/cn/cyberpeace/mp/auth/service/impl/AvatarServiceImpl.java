package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.auth.repository.AvatarMapper;
import cn.cyberpeace.mp.auth.repository.SysConfigMapper;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.auth.service.fields.AvatarFields;
import cn.cyberpeace.mp.auth.service.fields.SysConfigFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvatarServiceImpl extends BaseModelServiceImpl<AvatarModel, AvatarMapper, AvatarFields, QueryParams> implements AvatarService {

    @Autowired
    public AvatarServiceImpl(AvatarMapper modelMapper, AvatarFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public List<AvatarModel> listAll() {
        return getModelMapper().listAll();
    }
}
