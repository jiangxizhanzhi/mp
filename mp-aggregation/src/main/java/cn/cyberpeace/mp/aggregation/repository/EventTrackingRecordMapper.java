package cn.cyberpeace.mp.aggregation.repository;

import cn.cyberpeace.mp.aggregation.model.EventTrackingRecord;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(EventTrackingRecord.class)
public interface EventTrackingRecordMapper extends BaseModelMapper<EventTrackingRecord> {
}
