package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.WazuhProbeDataModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author wanggang
 */
@Mapper
@Repository
@ModelClass(WazuhProbeDataModel.class)
public interface WazuhProbeDataMapper extends BaseModelMapper<WazuhProbeDataModel> {

}
