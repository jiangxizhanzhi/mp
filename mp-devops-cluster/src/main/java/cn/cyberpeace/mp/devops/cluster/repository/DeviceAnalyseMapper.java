package cn.cyberpeace.mp.devops.cluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devops.cluster.model.DeviceAnalyseModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 运维管控-集群管理-计算节点-设备参数分析持久层
 * @author wanggang
 */
@Mapper
@Repository
@ModelClass(DeviceAnalyseModel.class)
public interface DeviceAnalyseMapper extends BaseModelMapper<DeviceAnalyseModel> {

}
