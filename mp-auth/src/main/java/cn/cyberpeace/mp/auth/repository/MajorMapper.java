package cn.cyberpeace.mp.auth.repository;


import cn.cyberpeace.mp.auth.model.MajorModel;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(MajorModel.class)
public interface MajorMapper extends BaseModelMapper<MajorModel> {
}
