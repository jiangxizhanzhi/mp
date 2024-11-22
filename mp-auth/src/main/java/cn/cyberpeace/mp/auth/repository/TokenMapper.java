package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.TokenModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/6 14:23
 */
@Mapper
@Repository
@ModelClass(TokenModel.class)
public interface TokenMapper extends BaseModelMapper<TokenModel> {
}
