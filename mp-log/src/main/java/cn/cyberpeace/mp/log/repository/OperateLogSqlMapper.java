package cn.cyberpeace.mp.log.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.log.bo.UserModelBoNameAndId;
import cn.cyberpeace.mp.log.model.OperateLogSqlModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/13
 */
@Mapper
@Repository
@ModelClass(OperateLogSqlModel.class)
public interface OperateLogSqlMapper extends BaseModelMapper<OperateLogSqlModel> {
    List<UserModelBoNameAndId> selectByUserId();
}
