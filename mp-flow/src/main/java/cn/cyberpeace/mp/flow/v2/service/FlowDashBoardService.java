package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.flow.v2.bo.FlowTaskCountQueryBO;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskCountMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
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

    public void count(String flowGeneratorId, String flowTaskRecordId, List<Date> dates) {
        var queryList = dates.stream().map(date -> {
            FlowTaskCountQueryBO query = new FlowTaskCountQueryBO();
            query.setStartTime(date);
            query.setEndTime(DateUtils.addSeconds(date, 10));
            return query;
        }).collect(Collectors.toList());
        var res = flowTaskCountMapper.countTask(flowGeneratorId, flowTaskRecordId, queryList);
        int a = 0;
    }
}
