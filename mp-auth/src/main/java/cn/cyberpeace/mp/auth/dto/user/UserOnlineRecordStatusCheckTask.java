package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 定时任务，用于更新用于在线记录表中的状态
 * @Author zhouxinghuai
 * @Date：2021/10/22 11:13
 */
@Slf4j
public class UserOnlineRecordStatusCheckTask implements Runnable {

    private ApplicationContext applicationContext;

    public UserOnlineRecordStatusCheckTask(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        log.info("FINISH用户在线记录信息...");
        // 更新用户在线记录信息
        // 1. 查找end_time超过5分钟，且status还是0的记录
        UserOnlineRecordService userOnlineRecordService = applicationContext.getBean(UserOnlineRecordService.class);

        List<UserOnlineRecordModel> offlineList = userOnlineRecordService.listCurrentOfflined(null);

        if (CollectionUtil.isNotEmpty(offlineList)) {
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

}
