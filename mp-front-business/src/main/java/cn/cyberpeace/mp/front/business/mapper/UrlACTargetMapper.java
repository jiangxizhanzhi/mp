package cn.cyberpeace.mp.front.business.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.front.business.model.UrlACTargetModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 19:50
 */
@Mapper
@Repository
@ModelClass(UrlACTargetModel.class)
public interface UrlACTargetMapper extends BaseModelMapper<UrlACTargetModel> {

    int deleteByUrlACId(String[] urlacId);

}
