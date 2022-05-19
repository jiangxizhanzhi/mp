package cn.cyberpeace.mp.front.business.mapper;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.front.business.model.UrlACModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 19:50
 */
@Mapper
@Repository
@ModelClass(UrlACModel.class)
public interface UrlACMapper extends BaseModelMapper<UrlACModel> {

}
