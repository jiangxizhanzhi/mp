 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOnlineRecordStatusCheckTask.java.
 // File size 1711.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service.task;

import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.base.common.BeanCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName UserOnlineRecordStatusCheckTask
 * @Description 定时任务，用于更新用于在线记录表中的状态
 * @Author zhouxinghuai
 * @Date 2021/10/22 11:13
 * @Version 1.0.0
 * @see Runnable
 */
@Slf4j
public class UserOnlineRecordStatusCheckTask implements Runnable {

    /**
     * run
     */
    @Override
    public void run() {
        // 更新用户在线记录信息
        // 1. 查找end_time超过5分钟，且status还是0的记录
        UserOnlineRecordService userOnlineRecordService = BeanCommon.getBean(UserOnlineRecordService.class);
        List<UserOnlineRecordModel> offlineList = userOnlineRecordService.listCurrentOfflined(null);

        if (CollectionUtils.isNotEmpty(offlineList)) {
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
