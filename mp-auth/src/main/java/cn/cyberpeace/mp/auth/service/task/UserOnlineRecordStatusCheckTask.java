 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOnlineRecordStatusCheckTask.java.
 // File size 1592.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service.task;

import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

 /**
  * 用户在线记录状态检查任务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
public class UserOnlineRecordStatusCheckTask implements Runnable {

     /**
      * 运行
      */
     @Override
    public void run() {
        // 更新用户在线记录信息
        // 1. 查找end_time超过5分钟，且status还是0的记录
        UserOnlineRecordService userOnlineRecordService = BeanCommon.getBean(UserOnlineRecordService.class);
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
