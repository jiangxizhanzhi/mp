package cn.cyberpeace.mp.collection.service.repo;

import cn.cyberpeace.mp.collection.model.log.DeviceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.Query;

/**
 * @ClassName CommonDeviceLogRepoCustom
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/29 11:33
 **/
public interface CommonDeviceLogRepoCustom {

    Page<DeviceLog> pageDeviceLogByQuery(Query query);

}
