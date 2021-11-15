package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.TargetApiModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/01 14:13
 */
@Mapper
@Repository
@ModelClass(TargetApiModel.class)
public interface TargetApiMapper extends BaseModelMapper<TargetApiModel> {

}
