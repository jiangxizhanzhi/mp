package cn.cyberpeace.mp.datasandtable.repository;

import cn.cyberpeace.mp.datasandtable.model.ProbeModel;
import cn.cyberpeace.mp.datasandtable.model.ProbeSearchRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProbeModelMapper {
    int deleteByPrimaryKey(Integer pk);

    int insert(ProbeModel record);

    int insertSelective(ProbeModel record);

    ProbeModel selectByPrimaryKey(Integer pk);

    int updateByPrimaryKeySelective(ProbeModel record);

    int updateByPrimaryKeyWithBLOBs(ProbeModel record);

    int updateByPrimaryKey(ProbeModel record);

    int deleteByProbeId(@Param("probeId") String probeId);

    List<ProbeModel> queryListWithPage(@Param("probeSearchRequest") ProbeSearchRequest probeSearchRequest, @Param("offset") Integer offset);

    Integer countQuery(ProbeSearchRequest probeSearchRequest);

    ProbeModel queryByProbeId(@Param("probeId") String probeId);

    int updateByProbeId(ProbeModel record);
}