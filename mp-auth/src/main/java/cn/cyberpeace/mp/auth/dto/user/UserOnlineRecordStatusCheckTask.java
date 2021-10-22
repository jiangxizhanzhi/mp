package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 定时任务，用于更新用于在线记录表中的状态
 * @Author zhouxinghuai
 * @Date：2021/10/22 11:13
 */
@Slf4j
public class UserOnlineRecordStatusCheckTask implements Runnable {


    private String userId;

    private UserOnlineRecordService userOnlineRecordService;

    public UserOnlineRecordStatusCheckTask(String userId, UserOnlineRecordService userOnlineRecordService) {
        this.userId = userId;
        this.userOnlineRecordService = userOnlineRecordService;
    }

    @Override
    public void run() {
        // 更新用户在线记录信息
        // 1. 查找end_time超过一定时间，status还是0的记录
        List<UserOnlineRecordModel> offlineList = userOnlineRecordService.listOfflined(null);

        // 2. 更改为已下线，即status改为1
        List<UserOnlineRecordModel> bulkUpdateList = new ArrayList<>();
        for (UserOnlineRecordModel model : offlineList) {
            UserOnlineRecordModel updateRecord = UserOnlineRecordModel.builder()
                    .id(model.getId())
                    .status(UserOnlineRecordModel.STATUS_FINISHED)
                    .build();
            bulkUpdateList.add(updateRecord);
        }
        userOnlineRecordService.bulkUpdate(bulkUpdateList);
    }

}
