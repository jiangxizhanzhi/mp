package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TargetMapper extends BaseModelMapper<TargetModel> {

}
