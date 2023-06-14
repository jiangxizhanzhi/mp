package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/6/12
 */
@Service
@Slf4j
public class UserDelayRemindLicenseTimeService {
    @Autowired
    private UserMapper userMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    private void updateUserAgreePrivacyPolicyTime() {
        log.info("start update user delay remind license time,now time is {}", LocalDateTime.now());
        var users = userMapper.list(new HumanCondition()
                .filterGreaterThanOrEqual(UserModel::getDelayRemindLicenseTime, 0));
        if (CollectionUtil.isNotEmpty(users)) {
            try {
                users.forEach(u -> {
                    u.setDelayRemindLicenseTime(u.getDelayRemindLicenseTime() - 1);
                });
                userMapper.bulkUpdate(users);
            } catch (Exception e) {
                log.error("update user delay remind license time error", e);
            }
        }
        log.info("end update user delay remind license time,now time is {}", LocalDateTime.now());
    }
}