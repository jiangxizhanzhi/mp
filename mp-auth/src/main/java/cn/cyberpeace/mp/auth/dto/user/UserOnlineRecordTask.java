package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 创建或者更新用户在线记录
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
public class UserOnlineRecordTask implements Runnable {


    private String userId;

    private UserOnlineRecordService userOnlineRecordService;

    public UserOnlineRecordTask(String userId, UserOnlineRecordService userOnlineRecordService) {
        this.userId = userId;
        this.userOnlineRecordService = userOnlineRecordService;
    }

    @Override
    public void run() {
        // 更新用户在线记录信息
        // 1. 查询状态为未完成的记录，然后更新duration_minutes, end_time
        List<UserOnlineRecordModel> onlineRecordList = userOnlineRecordService.listOnlined(userId);

        List<UserOnlineRecordModel> bulkUpdateList = new ArrayList<>();
        for (UserOnlineRecordModel model : onlineRecordList) {
            LocalDateTime startTime = model.getStartTime();
            LocalDateTime endTime = LocalDateTime.now();
            Duration duration = Duration.between(startTime, endTime);
            long durationMinutes = duration.toMinutes();
            Integer durationMin = Integer.valueOf(String.valueOf(durationMinutes));

            UserOnlineRecordModel updateRecord = UserOnlineRecordModel.builder()
                    .id(model.getId())
//                    .status(UserOnlineRecordModel.STATUS_FINISHED)
                    .durationMinutes(durationMin)
                    .endTime(endTime)
                    .build();

            bulkUpdateList.add(updateRecord);
        }
        userOnlineRecordService.bulkUpdate(bulkUpdateList);

    }

}
