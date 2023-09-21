package cn.cyberpeace.mp.collection.service.repo;

import cn.cyberpeace.mp.collection.model.log.CommonDeviceLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface CommonDeviceLogRepo  extends ElasticsearchRepository<CommonDeviceLog,String> {
}
