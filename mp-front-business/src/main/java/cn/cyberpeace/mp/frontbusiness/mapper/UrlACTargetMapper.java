package cn.cyberpeace.mp.frontbusiness.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.frontbusiness.model.UrlACTargetModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
@ModelClass(UrlACTargetModel.class)
public interface UrlACTargetMapper extends BaseModelMapper<UrlACTargetModel> {

}
