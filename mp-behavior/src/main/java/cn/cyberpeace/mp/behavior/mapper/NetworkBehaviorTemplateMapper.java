package cn.cyberpeace.mp.behavior.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.behavior.model.NetworkBehaviorTemplateModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(NetworkBehaviorTemplateModel.class)
public interface NetworkBehaviorTemplateMapper extends BaseModelMapper<NetworkBehaviorTemplateModel> {


}
