package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.model.EventTrackingRecord;
import cn.cyberpeace.mp.aggregation.repository.EventTrackingRecordMapper;
import cn.cyberpeace.mp.aggregation.request.EventTrackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName EventTrackingService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Service
public class EventTrackingService {
    @Autowired
    private EventTrackingRecordMapper recordMapper;

    /**
     * save
     *
     * @param request
     */
    public void save(EventTrackRequest request) {
        EventTrackingRecord record = new EventTrackingRecord();
        record.setAction(request.getAction());
        record.setModule(request.getModule());
        record.setTarget(request.getTarget());
        record.setCategory(request.getCategory());
        recordMapper.save(record);
    }
}
