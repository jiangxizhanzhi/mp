package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseModelServiceImpl<UserModel, UserMapper, UserFields>
        implements UserService {

    @Autowired
    public UserServiceImpl(UserMapper modelMapper, UserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
