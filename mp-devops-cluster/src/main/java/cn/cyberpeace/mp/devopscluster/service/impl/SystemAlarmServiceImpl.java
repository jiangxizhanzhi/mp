package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.enums.AlarmStatusEnum;
import cn.cyberpeace.mp.devopscluster.model.SystemAlarmModel;
import cn.cyberpeace.mp.devopscluster.repository.SystemAlarmMapper;
import cn.cyberpeace.mp.devopscluster.request.alarm.AlarmDistributionRequest;
import cn.cyberpeace.mp.devopscluster.request.alarm.AlarmQueryParams;
import cn.cyberpeace.mp.devopscluster.service.SystemAlarmService;
import cn.cyberpeace.mp.devopscluster.service.fields.SystemAlarmFields;
import cn.cyberpeace.mp.devopscluster.vo.AlarmCountVO;
import cn.cyberpeace.mp.devopscluster.vo.AlarmDistributionVo;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

                
                if (CollectionUtils.isNotEmpty(updateAlarmList)) {
                    this.getModelMapper().bulkUpdate(updateAlarmList);
                }

                
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
            
            enabledAlarms.forEach(enabledAlarm -> {
                enabledAlarm.setAlarmStatus(AlarmStatusEnum.DISABLED.toValue());
                enabledAlarm.setUpdateTime(LocalDateTime.now());
            });
            this.getModelMapper().bulkUpdate(enabledAlarms);
        }
    }

    
    @Override
    public List<AlarmCountVO> todayAlarmCount() {
        return this.getModelMapper().todayAlarmCount(LocalDateTime.now());
    }

    @Override
    public ListObjServiceResult<SystemAlarmModel> listSystemAlarm(AlarmQueryParams queryParams) {

        HumanCondition condition = new HumanCondition();
        if (ArrayUtil.isNotEmpty(queryParams.getAlarmStatus())) {
            condition.filterIn(SystemAlarmModel::getAlarmStatus, queryParams.getAlarmStatus());
        }

        if (ArrayUtil.isNotEmpty(queryParams.getAlarmTypes())) {
            condition.filterIn(SystemAlarmModel::getAlarmType, queryParams.getAlarmTypes());
        }

        if (StringUtils.isNotBlank(queryParams.getHostName())) {
            condition.filterEqual(SystemAlarmModel::getHostname, queryParams.getHostName());
        }

        if (ArrayUtil.isNotEmpty(queryParams.getOrders())) {
            condition.orderBy(queryParams.getOrders());
        }

        if (Objects.nonNull(queryParams.getPage()) && Objects.nonNull(queryParams.getPageSize())) {
            condition.pagination(queryParams.getPage(), queryParams.getPageSize());
        }

        var list = getModelMapper().list(condition);
        int total = getModelMapper().total(condition);
        return new ListObjServiceResult<>(new ListObjResult<>(list, total));

    }

    @Override
    public ServiceResult<AlarmDistributionVo> getSystemAlarmDistribution(AlarmDistributionRequest request) {
        HumanCondition humanCondition = new HumanCondition()
                .filterGreaterThanOrEqual(SystemAlarmModel::getCreateTime, request.getStartTime())
                .filterLessThanOrEqual(SystemAlarmModel::getCreateTime, request.getEndTime())
                .selectAs(Count.f(SystemAlarmModel::getPk), SystemAlarmModel.QUERY_FIELD_CNT)
                .select(SystemAlarmModel::getAlarmType)
                .groupBy(SystemAlarmModel::getAlarmType);
        List<Map<String, Object>> resultMaps = getModelMapper().originList(humanCondition);
        AlarmDistributionVo distributionVo = new AlarmDistributionVo();
        resultMaps.forEach(map -> {
            Integer alarmType = (Integer) map.get(SystemAlarmModel.QUERY_FIELD_ALARM_TYPE);
            Long count = (Long) map.getOrDefault(SystemAlarmModel.QUERY_FIELD_CNT, 0);
            switch (alarmType) {
                case 0:
                    distributionVo.setCpAlarmCount(count);
                    break;
                case 1:
                    distributionVo.setCrAlarmCount(count);
                    break;
                case 2:
                    distributionVo.setServerAlarmCount(count);
                    break;
            }
        });
        return new ServiceResult<>(distributionVo).asResult();
    }
}
