package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ClientDetailsModel.class)
public interface ClientDetailsMapper extends BaseModelMapper<ClientDetailsModel> {

}
