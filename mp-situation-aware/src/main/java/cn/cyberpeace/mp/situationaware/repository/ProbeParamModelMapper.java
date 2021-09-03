package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProbeParamModelMapper {
    int deleteByPrimaryKey(Integer pk);

    int insert(ProbeParamModel record);

    int insertSelective(ProbeParamModel record);

    ProbeParamModel selectByPrimaryKey(Integer pk);

    int updateByPrimaryKeySelective(ProbeParamModel record);

    int updateByPrimaryKey(ProbeParamModel record);

    int batchInsert(@Param("paramModelList") List<ProbeParamModel> paramModelList);

    int deleteParamByProbeId(@Param("probeId") String probeId);

    List<ProbeParamModel> queryProbeParamList(@Param("probeId") String probeId);

}