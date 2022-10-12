package cn.cyberpeace.mp.log.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.log.model.OperationLogDbModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/13
 */
@Mapper
@Repository
@ModelClass(OperationLogDbModel.class)
public interface OperationLogDbMapper extends BaseModelMapper<OperationLogDbModel> {
}
