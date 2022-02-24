package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description: 创建或者更新用户在线记录
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
public class UserOnlineRecordTask implements Runnable {

    private String userId;

    private CopyOnWriteArrayList<LocalDateTime> timestampList;

    private ApplicationContext applicationContext;

    public UserOnlineRecordTask(String userId, CopyOnWriteArrayList<LocalDateTime> timestampList, ApplicationContext applicationContext) {
        this.userId = userId;
        this.timestampList = timestampList;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        log.info("create or update UserOnlineRecord...");
        // 更新用户在线记录信息
        // 1. 查询状态为未完成的记录，然后更新duration_minutes, end_time
        UserOnlineRecordService userOnlineRecordService = applicationContext.getBean(UserOnlineRecordService.class);
        List<UserOnlineRecordModel> onlinedRecordList = userOnlineRecordService.listCurrentOnlined(userId);

        // 存在当前用户的在线记录，直接更新
        if (CollectionUtil.isNotEmpty(onlinedRecordList)) {
            log.info("UserOnlineRecord existed, updating...");
            List<UserOnlineRecordModel> bulkUpdateList = new ArrayList<>();
            for (UserOnlineRecordModel model : onlinedRecordList) {
                LocalDateTime startTime = model.getStartTime();
                LocalDateTime endTime = LocalDateTime.now();
                Duration duration = Duration.between(startTime, endTime);
                long durationMinutes = duration.toMinutes();
                Integer durationMin = Integer.valueOf(String.valueOf(durationMinutes));

                UserOnlineRecordModel updateRecord = UserOnlineRecordModel.builder()
                        .id(model.getId())
                        .durationMinutes(durationMin)
                        .endTime(endTime)
                        .build();

                bulkUpdateList.add(updateRecord);
            }
            userOnlineRecordService.bulkUpdate(bulkUpdateList);

        } else {
            // 不存在当前用户的在线记录，新增一个新的记录
            log.info("UserOnlineRecord not exist, creating...");
            UserService userService = applicationContext.getBean(UserService.class);
            var source = userService.get(userId).getSource();
            UserModel user = Objects.isNull(source) ? null : source.getObj();

            if (Objects.nonNull(user)) {
//                UserDto userDto =  userService.doModel2Dto(user, true, false);
//                String orgpaths = userDto.getOrgs().stream().map(OrgModel::getPath).collect(Collectors.joining(","));

                // 如果存在第一次心跳时间，则startTime=第一次心跳时间
                LocalDateTime startTime = null;
                LocalDateTime firstTime = timestampList.get(0);
                if (Objects.nonNull(firstTime)) {
                    startTime = firstTime;
                } else {
                    // 否则startTime=now() - 2m
                    startTime = LocalDateTime.now().minusMinutes(2);
                }

                LocalDateTime endTime = LocalDateTime.now();
                Duration duration = Duration.between(startTime, endTime);
                long durationMinutes = duration.toMinutes();
                Integer durationMin = Integer.valueOf(String.valueOf(durationMinutes));
                durationMin = durationMin < 1 ? 1 : durationMin;

                UserOnlineRecordModel onlineRecordModel = UserOnlineRecordModel.builder()
                        .user(userId)
                        .username(user.getUsername())
                        .name(user.getName())
                        .durationMinutes(durationMin)
                        .startTime(startTime).build();

                userOnlineRecordService.create(onlineRecordModel);
            }

        }

    }

}
