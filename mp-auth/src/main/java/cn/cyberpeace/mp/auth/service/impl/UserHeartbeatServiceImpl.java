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

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class UserHeartbeatServiceImpl extends BaseModelServiceImpl<UserHeartbeatModel, UserHeartbeatMapper, UserHeartbeatFields, QueryParams> implements UserHeartbeatService {

    private static ConcurrentHashMap<String, Integer> heartbeatMap = new ConcurrentHashMap<>();


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
            String userId = userModel.getId();
            Integer count = heartbeatMap.get(userId);
            if (Objects.nonNull(count)) {
                heartbeatMap.put(userId, count++);
            } else {
                heartbeatMap.put(userId, 1);
            }
        } else {
            log.error("没有查询到有效登录用户信息！");
        }
    }

    @Override
    public ConcurrentHashMap<String, Integer> getHeartbeatMap() {
        return heartbeatMap;
    }

    @Override
    public void clearHeartbeatmap() {
        heartbeatMap.clear();
    }
}
