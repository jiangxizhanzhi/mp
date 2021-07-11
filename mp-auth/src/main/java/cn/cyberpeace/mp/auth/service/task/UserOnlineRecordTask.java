package cn.cyberpeace.mp.auth.service.task;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserOnlineRecordModel;
import cn.cyberpeace.mp.auth.service.UserOnlineRecordService;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
public class UserOnlineRecordTask implements Runnable {

    private Map<UserModel, LocalDateTime> userModel2LastTsMap;

    public UserOnlineRecordTask(Map<UserModel, LocalDateTime> userModel2LastTsMap) {
        this.userModel2LastTsMap = userModel2LastTsMap;
    }

    @Override
    public void run() {
        
        
        UserOnlineRecordService userOnlineRecordService = BeanCommon.getBean(UserOnlineRecordService.class);

        List<UserModel> userModels = userModel2LastTsMap.keySet().stream().collect(Collectors.toList());
        List<String> userIds = userModels.stream().map(UserModel::getId).collect(Collectors.toList());
        List<UserOnlineRecordModel> onlinedRecordList = userOnlineRecordService.listCurrentOnlined(userIds);
        List<String> onlinedRecordedUserIds = onlinedRecordList.stream().map(UserOnlineRecordModel::getUser).collect(Collectors.toList());
        List<UserModel> notRecordedUserModels = userModels.stream().filter(model -> !onlinedRecordedUserIds.contains(model.getId())).collect(Collectors.toList());


        
        if (CollectionUtil.isNotEmpty(onlinedRecordList)) {
            List<UserOnlineRecordModel> bulkUpdateList = new ArrayList<>();
            for (UserOnlineRecordModel recordModel : onlinedRecordList) {
                LocalDateTime startTime = recordModel.getStartTime();
                LocalDateTime endTime = LocalDateTime.now();
                Duration duration = Duration.between(startTime, endTime);
                long durationMinutes = duration.toMinutes();
                Integer durationMin = Integer.valueOf(String.valueOf(durationMinutes));

                UserOnlineRecordModel updateRecord = UserOnlineRecordModel.builder()
                        .id(recordModel.getId())
                        .durationMinutes(durationMin)
                        .endTime(endTime)
                        .build();

                bulkUpdateList.add(updateRecord);
            }

            userOnlineRecordService.bulkUpdate(bulkUpdateList);
        }


        if (CollectionUtil.isNotEmpty(notRecordedUserModels)) {
            
            List<UserOnlineRecordModel> bulkList = new ArrayList<>();
            for (UserModel userModel : notRecordedUserModels) {


                
                LocalDateTime startTime = null;
                LocalDateTime firstTime = userModel2LastTsMap.get(userModel);
                if (Objects.nonNull(firstTime)) {
                    startTime = firstTime;
                } else {
                    
                    startTime = LocalDateTime.now().minusMinutes(2);
                }

                LocalDateTime endTime = LocalDateTime.now();
                Duration duration = Duration.between(startTime, endTime);
                long durationMinutes = duration.toMinutes();
                Integer durationMin = Integer.valueOf(String.valueOf(durationMinutes));
                durationMin = durationMin < 1 ? 1 : durationMin;

                UserOnlineRecordModel onlineRecordModel = UserOnlineRecordModel.builder()
                        .user(userModel.getId())
                        .durationMinutes(durationMin)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();

                bulkList.add(onlineRecordModel);
            }
            if (CollectionUtil.isNotEmpty(bulkList)) {
                userOnlineRecordService.bulkCreate(bulkList);
            }
        }

    }

}
