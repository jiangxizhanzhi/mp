 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserDelayRemindLicenseTimeService.java.
 // File size 1272.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserDelayRemindLicenseTimeService
 * @Description
 * @Author zhanglin
 * @Date 2023/6/12
 * @Version 1.0.0
 */
@Service
@Slf4j
public class UserDelayRemindLicenseTimeService {
//    @Autowired
//    private UserMapper userMapper;
//
//    @Scheduled(cron = "0 0 0 * * ?")
//    private void updateUserAgreePrivacyPolicyTime() {
//        log.info("start update user delay remind license time,now time is {}", LocalDateTime.now());
//        var users = userMapper.list(new HumanCondition()
//                .filterGreaterThanOrEqual(UserModel::getDelayRemindLicenseTime, 0));
//        if (CollectionUtil.isNotEmpty(users)) {
//            try {
//                users.forEach(u -> {
//                    u.setDelayRemindLicenseTime(u.getDelayRemindLicenseTime() - 1);
//                });
//                userMapper.bulkUpdate(users);
//            } catch (Exception e) {
//                log.error("update user delay remind license time error", e);
//            }
//        }
//        log.info("end update user delay remind license time,now time is {}", LocalDateTime.now());
//    }
}