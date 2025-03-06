package cn.cyberpeace.mp.behavior.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.behavior.model.RunBehaviorLogModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(RunBehaviorLogModel.class)
public interface RunBehaviorLogMapper extends BaseModelMapper<RunBehaviorLogModel> {


}
