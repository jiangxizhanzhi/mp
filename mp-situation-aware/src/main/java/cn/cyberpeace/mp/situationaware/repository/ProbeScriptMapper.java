package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.ProbeScriptModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ProbeScriptModel.class)
public interface ProbeScriptMapper extends BaseModelMapper<ProbeScriptModel> {
    //    int deleteByPrimaryKey(Integer pk);
//
//    int insert(ProbeScriptModel record);
//
//    int insertSelective(ProbeScriptModel record);
//
//    ProbeScriptModel selectByPrimaryKey(Integer pk);
//
//    int updateByPrimaryKeySelective(ProbeScriptModel record);
//
//    int updateByPrimaryKeyWithBLOBs(ProbeScriptModel record);
//
//    int updateByPrimaryKey(ProbeScriptModel record);
    int deleteByProbeId(String probeId);

    String getChecksumByProbeId(String probeId);
}
