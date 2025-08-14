package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.AttackCommandHistoryModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(AttackCommandHistoryModel.class)
public interface AttackCommandHistoryMapper extends BaseModelMapper<AttackCommandHistoryModel> {
}
