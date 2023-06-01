package cn.cyberpeace.mp.log.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.log.po.MpLogLevelPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(MpLogLevelPO.class)
public interface MpLogLevelPOMapper extends BaseModelMapper<MpLogLevelPO> {

      int deleteAll();

}