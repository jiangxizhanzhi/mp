package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.flow.v2.bo.FlowBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowBandwidthCountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowDashBoardCountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBandwidthCountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskBandwidthCountBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskCountQueryBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.bo.ValueBO;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskCountResultModel;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskCountMapper;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskRecordMapper;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2022/11/1
 */
@Service
public class FlowDashBoardService {
    @Resource
    private FlowTaskCountMapper flowTaskCountMapper;
    
    @Resource
    private FlowTaskRecordMapper flowTaskRecordMapper;

    public ServiceResult<FlowGeneratorBandwidthCountBO> countFlowGenerator(String flowGeneratorId, String start, String end) {
        var queryList = getQueryList(start, end);
        var res = flowTaskCountMapper.countTask(flowGeneratorId, queryList);
        FlowGeneratorBandwidthCountBO countBO = new FlowGeneratorBandwidthCountBO();
        countBO.setFlowGeneratorId(flowGeneratorId);
        Map<String, FlowTaskBandwidthCountBO> taskId2Task = new HashMap<>();
        Map<String, ValueBO> time2Value = new HashMap<>();
        long total = 0L;
        for (FlowTaskCountResultModel flowTaskCountResultModel : res) {
            total = flowTaskCountResultModel.getBandwidth() + total;

            String taskId = flowTaskCountResultModel.getFlowTaskRecordId();
            if (Objects.isNull(taskId2Task.get(taskId))) {
                FlowTaskBandwidthCountBO flowTaskBandwidthCountBO = new FlowTaskBandwidthCountBO();
                flowTaskBandwidthCountBO.setValues(new ArrayList<>());
                flowTaskBandwidthCountBO.setPercentages(new ArrayList<>());
                taskId2Task.put(taskId, flowTaskBandwidthCountBO);
            }
            var taskBandwidth = taskId2Task.get(taskId);
            taskBandwidth.setFlowTaskId(taskId);
            ValueBO valueBO = new ValueBO();
            valueBO.setTime(TimeUtils.localDateTimeToString(flowTaskCountResultModel.getTime()));
            valueBO.setValue(flowTaskCountResultModel.getBandwidth().doubleValue());
            taskBandwidth.getValues().add(valueBO);

            String time = TimeUtils.localDateTimeToString(flowTaskCountResultModel.getTime());
            if (Objects.isNull(time2Value.get(time))) {
                time2Value.put(time, new ValueBO());
            }
            var value = time2Value.get(time);
            value.setTime(time);
            double v = value.getValue() == null ? 0 : value.getValue();
            value.setValue(v + flowTaskCountResultModel.getBandwidth());
        }
        countBO.setTasks(new ArrayList<>(taskId2Task.values()));
        countBO.setTotal(total);
        countBO.setValues(new ArrayList<>(time2Value.values()));
        countBO.getTasks().forEach(flowTaskBandwidthCountBO -> flowTaskBandwidthCountBO.getValues().forEach(valueBO -> {
            String time = valueBO.getTime();
            var value = time2Value.get(time);
            ValueBO percent = new ValueBO();
            percent.setTime(time);
            percent.setValue(valueBO.getValue() / value.getValue());
            flowTaskBandwidthCountBO.getPercentages().add(percent);
        }));
        return new ServiceResult<>(countBO);
    }

    public ServiceResult<FlowTaskBandwidthCountBO> countFlowRecord(String flowGeneratorId, String flowTaskRecordId, String start,
                                                                   String end) {
        var queryList = getQueryList(start, end);
        var res = flowTaskCountMapper.countFlow(flowGeneratorId, flowTaskRecordId, queryList);
        FlowTaskBandwidthCountBO countBO = new FlowTaskBandwidthCountBO();
        countBO.setFlowTaskId(flowTaskRecordId);
        Map<String, FlowBandwidthCountBO> flowId2Flow = new HashMap<>();
        Map<String, ValueBO> time2Value = new HashMap<>();
        long total = 0L;
        for (FlowTaskCountResultModel flowTaskCountResultModel : res) {
            total = flowTaskCountResultModel.getBandwidth() + total;

            String flowId = flowTaskCountResultModel.getFlowId();
            if (Objects.isNull(flowId2Flow.get(flowTaskCountResultModel.getFlowId()))) {
                FlowBandwidthCountBO flowBandwidthCountBO = new FlowBandwidthCountBO();
                flowBandwidthCountBO.setValues(new ArrayList<>());
                flowBandwidthCountBO.setPercentages(new ArrayList<>());
                flowId2Flow.put(flowId, flowBandwidthCountBO);
            }
            var flowBandwidth = flowId2Flow.get(flowId);
            flowBandwidth.setFlowId(flowId);
            ValueBO valueBO = new ValueBO();
            valueBO.setTime(TimeUtils.localDateTimeToString(flowTaskCountResultModel.getTime()));
            valueBO.setValue(flowTaskCountResultModel.getBandwidth().doubleValue());
            flowBandwidth.getValues().add(valueBO);

            String time = TimeUtils.localDateTimeToString(flowTaskCountResultModel.getTime());
            if (Objects.isNull(time2Value.get(time))) {
                time2Value.put(time, new ValueBO());
            }
            var value = time2Value.get(time);
            value.setTime(time);
            double v = value.getValue() == null ? 0 : value.getValue();
            value.setValue(v + flowTaskCountResultModel.getBandwidth());
        }
        countBO.setFlows(new ArrayList<>(flowId2Flow.values()));
        countBO.setTotal(total);
        countBO.setValues(new ArrayList<>(time2Value.values()));
        countBO.getFlows().forEach(flowBandwidthCountBO -> flowBandwidthCountBO.getValues().forEach(valueBO -> {
            String time = valueBO.getTime();
            var value = time2Value.get(time);
            ValueBO percent = new ValueBO();
            percent.setTime(time);
            percent.setValue(valueBO.getValue() / value.getValue());
            flowBandwidthCountBO.getPercentages().add(percent);
        }));
        return new ServiceResult<>(countBO);
    }

    /**
     * 实时仿真数量统计
     *
     * @return
     */
    public ServiceResult<List<FlowDashBoardCountBO>> countStartTask() {
        var resultModels = flowTaskRecordMapper.countStartTask();
        List<FlowDashBoardCountBO> result = new ArrayList<>();
        resultModels.forEach(flowTaskCountResultModel -> {
            FlowDashBoardCountBO countBO = new FlowDashBoardCountBO();
            countBO.setName(flowTaskCountResultModel.getFlowGeneratorId());
            countBO.setCount(flowTaskCountResultModel.getCount());
            result.add(countBO);
        });
        return new ServiceResult<>(result);
    }

    /**
     * 实时总带宽统计
     *
     * @return
     */
    public ServiceResult<List<FlowDashBoardCountBO>> sumBandwidth() {
        FlowTaskCountQueryBO queryBO = new FlowTaskCountQueryBO();
        LocalDateTime endTime = LocalDateTime.now();
        queryBO.setEndTime(endTime);
        queryBO.setStartTime(endTime.plusSeconds(-5));
        var resultModels = flowTaskCountMapper.sumBandwidth(queryBO);
        List<FlowDashBoardCountBO> result = new ArrayList<>();
        resultModels.forEach(flowTaskCountResultModel -> {
            FlowDashBoardCountBO countBO = new FlowDashBoardCountBO();
            countBO.setName(flowTaskCountResultModel.getFlowGeneratorId());
            countBO.setCount(flowTaskCountResultModel.getBandwidth());
            result.add(countBO);
        });
        return new ServiceResult<>(result);
    }

    /**
     * 流量执行器仿真任务查询
     *
     * @return
     */
    public ListObjServiceResult<FlowTaskRecordBO> generatorTaskList(String flowGeneratorId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);

        PageInfo<FlowTaskRecordModel> pageInfo = new PageInfo<>(flowTaskRecordMapper.generatorTaskList(flowGeneratorId));
        List<FlowTaskRecordBO> taskBOs = pageInfo.getList().stream().map(t -> {
            FlowTaskRecordBO bo = new FlowTaskRecordBO();
            bo.fromModel(t);
            return bo;
        }).collect(Collectors.toList());

        return new ListObjServiceResult<>(new ListObjResult<>(taskBOs, (int) pageInfo.getTotal()));
    }

    /**
     * 仿真任务流查询
     *
     * @return
     */
    public ServiceResult<List<FlowBO>> taskFlowList(String recordId) {
        HumanCondition humanCondition = new HumanCondition().filterEqual(FlowTaskRecordModel::getId, recordId);
        var models = flowTaskRecordMapper.list(humanCondition);
        List<FlowBO> flows = JSON.parseArray(models.get(0).getFlows(), FlowBO.class);
        return new ServiceResult<>(flows);
    }

    private List<FlowTaskCountQueryBO> getQueryList(String start, String end) {
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (StringUtils.isEmpty(end)) {
            endTime = LocalDateTime.now();
        } else {
            endTime = TimeUtils.stringToLocalDateTime(end);
        }
        if (StringUtils.isEmpty(start)) {
            startTime = endTime.plusSeconds(-25);
        } else {
            startTime = TimeUtils.stringToLocalDateTime(start);
        }
        List<FlowTaskCountQueryBO> queryList = new ArrayList<>();
        while (endTime.isAfter(startTime)) {
            FlowTaskCountQueryBO queryBO = new FlowTaskCountQueryBO();
            queryBO.setStartTime(startTime);
            queryBO.setEndTime(startTime.plusSeconds(5));
            queryList.add(queryBO);
            startTime = startTime.plusSeconds(5);
        }
        return queryList;
    }

}
