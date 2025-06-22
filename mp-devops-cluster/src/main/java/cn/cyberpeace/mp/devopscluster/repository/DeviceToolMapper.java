package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.model.DeviceToolModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description 镜像工具信息mapper
 * @Author liyah
 * @Date 2025-6-19 10:07
 */
@Mapper
@Repository
@ModelClass(DeviceToolModel.class)
public interface DeviceToolMapper extends BaseModelMapper<DeviceToolModel> {

}
