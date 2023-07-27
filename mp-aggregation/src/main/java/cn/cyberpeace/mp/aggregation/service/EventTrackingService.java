package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.model.EventTrackingRecord;
import cn.cyberpeace.mp.aggregation.repository.EventTrackingRecordMapper;
import cn.cyberpeace.mp.aggregation.request.EventTrackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventTrackingService {
    @Autowired
    private EventTrackingRecordMapper recordMapper;

    public void save(EventTrackRequest request) {
        EventTrackingRecord record = new EventTrackingRecord();
        record.setAction(request.getAction());
        record.setModule(request.getModule());
        record.setTarget(request.getTarget());
        record.setCategory(request.getCategory());
        record.setOperationType(request.getOperationType());
        record.setCurrentUrl(request.getCurrentUrl());
        record.setTitle(request.getTitle());
        record.setParameter(request.getParameter());
        record.setUserId(request.getUserId());
        record.setUserName(request.getUserName());
        recordMapper.save(record);
    }
}
