package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.DeviceModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description
 * @Author shgang
 * @Date 2022/12/19
 */
@Mapper
@Repository
@ModelClass(DeviceModel.class)
public interface DeviceMapper extends BaseModelMapper<DeviceModel> {

}
