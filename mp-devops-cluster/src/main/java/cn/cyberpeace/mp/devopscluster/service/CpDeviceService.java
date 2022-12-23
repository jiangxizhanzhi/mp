package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.DeviceModel;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author shgang
 * @Date 2022/12/20
 */
public interface CpDeviceService extends BaseModelService<DeviceModel, QueryParams> {

    List<Device> findDeviceByIds(List<Integer> ids);

}
