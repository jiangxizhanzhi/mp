package cn.cyberpeace.mp.auth.service.task;

import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class UserOnlineRecordStatusCheckTask implements Runnable {

    @Override
    public void run() {
        
        
        UserOnlineRecordService userOnlineRecordService = BeanCommon.getBean(UserOnlineRecordService.class);
        List<UserOnlineRecordModel> offlineList = userOnlineRecordService.listCurrentOfflined(null);

        if (CollectionUtil.isNotEmpty(offlineList)) {
            
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
