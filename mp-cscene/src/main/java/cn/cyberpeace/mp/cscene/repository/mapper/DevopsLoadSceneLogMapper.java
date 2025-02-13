package cn.cyberpeace.mp.cscene.repository.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSceneLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(MpDevopsLoadSceneLog.class)
public interface DevopsLoadSceneLogMapper extends BaseModelMapper<MpDevopsLoadSceneLog> {

}
