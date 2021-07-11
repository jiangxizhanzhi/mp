package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.TargetDataModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
@ModelClass(TargetDataModel.class)
public interface TargetDataMapper extends BaseModelMapper<TargetDataModel> {

}
