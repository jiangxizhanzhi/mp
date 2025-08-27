package cn.cyberpeace.mp.devopscluster.repository;


import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.model.PhysicNodeModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(PhysicNodeModel.class)
public interface PhysicNodeMapper extends BaseModelMapper<PhysicNodeModel> {
}
