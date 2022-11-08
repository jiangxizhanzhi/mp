package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneAuthModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CsceneAuthModel.class)
public interface CsceneAuthMapper extends BaseModelMapper<CsceneAuthModel> {

}
