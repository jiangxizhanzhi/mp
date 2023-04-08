package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.ProbeOperateLogModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
@ModelClass(ProbeOperateLogModel.class)
public interface ProbeOperateLogMapper extends BaseModelMapper<ProbeOperateLogModel> {

}
