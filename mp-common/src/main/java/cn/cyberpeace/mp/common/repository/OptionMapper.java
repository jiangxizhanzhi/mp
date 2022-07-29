package cn.cyberpeace.mp.common.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.common.model.OptionModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
@Mapper
@Repository
@ModelClass(OptionModel.class)
public interface OptionMapper extends BaseModelMapper<OptionModel> {
}
