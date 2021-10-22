package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.repository.UserOnlineRecordMapper;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.service.fields.UserOnlineRecordFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class UserOnlineRecordServiceImpl extends BaseModelServiceImpl<UserOnlineRecordModel, UserOnlineRecordMapper, UserOnlineRecordFields, QueryParams> implements UserOnlineRecordService {

    @Autowired
    public UserOnlineRecordServiceImpl(UserOnlineRecordMapper modelMapper, UserOnlineRecordFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }


    @Override
    public PageInfo getOnlineUserPage() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(5);
        List<UserOnlineRecordModel> onlineRecordModelList = getModelMapper().listOnlined(null, deadline);
        log.info("{}", onlineRecordModelList);
        return null;
    }

    @Override
    public List<UserOnlineRecordModel> listOnlined(String userId) {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(5);
        List<UserOnlineRecordModel> onlineList = getModelMapper().listOnlined(userId, deadline);
        log.info("{}", onlineList);
        return onlineList;
    }

    @Override
    public List<UserOnlineRecordModel> listOfflined(String userId) {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(5);
        List<UserOnlineRecordModel> offlineList = getModelMapper().listOfflined(userId, deadline);
        log.info("{}", offlineList);
        return offlineList;
    }

}
