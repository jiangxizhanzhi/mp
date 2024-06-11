package cn.cyberpeace.mp.behavior.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.behavior.model.UserBehaviorModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(UserBehaviorModel.class)
public interface UserBehaviorMapper extends BaseModelMapper<UserBehaviorModel> {
}
