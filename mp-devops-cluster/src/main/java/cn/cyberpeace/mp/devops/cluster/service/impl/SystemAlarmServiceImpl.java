package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.cluster.enums.AlarmStatusEnum;
import cn.cyberpeace.mp.devops.cluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devops.cluster.repository.SystemAlarmMapper;
import cn.cyberpeace.mp.devops.cluster.service.SystemAlarmService;
import cn.cyberpeace.mp.devops.cluster.service.fields.SystemAlarmFields;
import cn.cyberpeace.mp.devops.cluster.vo.AlarmCountVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SystemAlarmServiceImpl extends BaseModelServiceImpl<SystemAlarmModel, SystemAlarmMapper, SystemAlarmFields,
        QueryParams> implements SystemAlarmService {

    public SystemAlarmServiceImpl(SystemAlarmMapper stuMapper, SystemAlarmFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @Override
    public void processSystemAlarm(List<SystemAlarmModel> systemAlarmModels) {
        // 查询当前生效的告警信息
        List<SystemAlarmModel> enabledAlarms =
                this.getModelMapper().list(new HumanCondition().filterEqual(SystemAlarmModel::getAlarmStatus,
                        AlarmStatusEnum.ENABLED.toValue()));

        if (CollectionUtils.isNotEmpty(systemAlarmModels)) {
            if (CollectionUtils.isNotEmpty(enabledAlarms)) {
                List<SystemAlarmModel> addAlarmList = new ArrayList<>();
                List<SystemAlarmModel> updateAlarmList = new ArrayList<>();
                List<SystemAlarmModel> delAlarmList = new ArrayList<>();
                systemAlarmModels.forEach(systemAlarmModel -> {
                    boolean isExist = false;
                    for (SystemAlarmModel enabledAlarm : enabledAlarms) {
                        if (systemAlarmModel.getServername().equals(enabledAlarm.getServername())
                                && Objects.equals(systemAlarmModel.getAlarmType(), enabledAlarm.getAlarmType())) {
                            // 已存在告警，更新告警时间
                            Duration duration = Duration.between(enabledAlarm.getCreateTime(), LocalDateTime.now());
                            int runningTime = Long.valueOf(duration.toMillis() / 1000).intValue();
                            enabledAlarm.setRunningTime(runningTime);
                            enabledAlarm.setUpdateTime(LocalDateTime.now());
                            enabledAlarm.setAlarmMsg(systemAlarmModel.getAlarmMsg());
                            updateAlarmList.add(enabledAlarm);
                            isExist = true;
                        }
                    }

                    if (!isExist) {
                        addAlarmList.add(systemAlarmModel);
                    }
                });

                // 本次更新告警
                if (CollectionUtils.isNotEmpty(updateAlarmList)) {
                    this.getModelMapper().bulkUpdate(updateAlarmList);
                }

                // 本次新增告警信息
                if (CollectionUtils.isNotEmpty(addAlarmList)) {
                    this.getModelMapper().bulkCreate(addAlarmList);
                }

                enabledAlarms.forEach(enabledAlarm -> {
                    boolean isExist = false;
                    for (SystemAlarmModel systemAlarmModel : systemAlarmModels) {
                        if (systemAlarmModel.getServername().equals(enabledAlarm.getServername())
                                && Objects.equals(systemAlarmModel.getAlarmType(), enabledAlarm.getAlarmType())) {
                            isExist = true;
                        }
                    }
                    if (!isExist) {
                        // 本次失效告警
                        enabledAlarm.setAlarmStatus(AlarmStatusEnum.DISABLED.toValue());
                        enabledAlarm.setUpdateTime(LocalDateTime.now());
                        delAlarmList.add(enabledAlarm);
                    }
                });
                if (CollectionUtils.isNotEmpty(delAlarmList)) {
                    this.getModelMapper().bulkUpdate(delAlarmList);
                }

            } else {
                this.getModelMapper().bulkCreate(systemAlarmModels);
            }

        } else {
            // 没有新告警信息，之前告警都设置为失效状态
            enabledAlarms.forEach(enabledAlarm -> {
                enabledAlarm.setAlarmStatus(AlarmStatusEnum.DISABLED.toValue());
                enabledAlarm.setUpdateTime(LocalDateTime.now());
            });
            this.getModelMapper().bulkUpdate(enabledAlarms);
        }
    }

    /**
     * 当天告警数量统计
     * @return
     */
    @Override
    public List<AlarmCountVO> todayAlarmCount() {
        return this.getModelMapper().todayAlarmCount();
    }
}
