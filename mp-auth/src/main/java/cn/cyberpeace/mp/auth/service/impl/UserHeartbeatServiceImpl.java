package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.user.UserOlReportCntDto;
import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.UserHeartbeatMapper;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.fields.UserHeartbeatFields;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class UserHeartbeatServiceImpl extends BaseModelServiceImpl<UserHeartbeatModel, UserHeartbeatMapper, UserHeartbeatFields, QueryParams> implements UserHeartbeatService {

    private static ConcurrentHashMap<UserModel, CopyOnWriteArrayList<LocalDateTime>> heartbeatMap = new ConcurrentHashMap<>();


    @Autowired
    public UserHeartbeatServiceImpl(UserHeartbeatMapper modelMapper, UserHeartbeatFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    public List<UserOlReportCntDto> groupCntByUserId(LocalDateTime time) {
        List<UserOlReportCntDto> list = getModelMapper().groupCntByUserId(time);
        return list;
    }

    @Override
    public void heartbeat() {
        UserModel userModel = UserUtils.getLoginUser();
        if (Objects.nonNull(userModel)) {
            CopyOnWriteArrayList<LocalDateTime> timestampList = heartbeatMap.get(userModel);
            if (Objects.nonNull(timestampList)) {
                timestampList.add(LocalDateTime.now());
            } else {
                timestampList = new CopyOnWriteArrayList();
                timestampList.add(LocalDateTime.now());
                heartbeatMap.put(userModel, timestampList);
            }
        } else {
            log.error("not existed illegal logined user!");
        }
    }

    @Override
    public ConcurrentHashMap<UserModel, CopyOnWriteArrayList<LocalDateTime>> getHeartbeatMap() {
        return heartbeatMap;
    }

    @Override
    public void clearHeartbeatmap() {
        heartbeatMap.clear();
    }
}
