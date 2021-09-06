package cn.cyberpeace.mp.devops.cluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devops.cluster.model.TargetLogoModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(TargetLogoModel.class)
public interface TargetLogoMapper extends BaseModelMapper<TargetLogoModel> {


}
