package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.LocalLoadExecutionRecordModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author liyahao
* @description 针对表【mp_local_load_execution_record(本地载荷执行记录表)】的数据库操作Mapper
* @createDate 2024-12-27 17:28:05
* @Entity generator.cn/cyberpeace/mp/cscene.MpLocalLoadExecutionRecord
*/

@Mapper
@Repository
@ModelClass(LocalLoadExecutionRecordModel.class)
public interface LocalLoadExecutionRecordMapper extends BaseModelMapper<LocalLoadExecutionRecordModel> {

}




