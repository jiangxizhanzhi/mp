//package cn.cyberpeace.mp.situationaware.repository;
//
//import cn.cyberpeace.mp.situationaware.model.ProbeModel;
//import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchRequest;
//import cn.cyberpeace.mp.situationaware.model.enums.ProbeStatus;
//import java.util.List;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//import org.springframework.stereotype.Repository;
//
//@Mapper
//@Repository
//public interface ProbeModelMapper {
//    int deleteByPrimaryKey(Integer pk);
//
//    int insert(ProbeModel record);
//
//    ProbeModel selectByPrimaryKey(Integer pk);
//
//    int deleteByProbeId(@Param("probeId") String probeId);
//
//    int deleteByProbeIds(@Param("probeIds") List<String> probeIds);
//
//    List<ProbeModel> queryListWithPage(@Param("probeSearchRequest") ProbeSearchRequest probeSearchRequest, @Param("offset") Integer offset);
//
//    Integer countQuery(ProbeSearchRequest probeSearchRequest);
//
//    ProbeModel queryByProbeId(@Param("probeId") String probeId);
//
//    List<ProbeModel> queryByProbeIds(@Param("probeIds") List<String> probeIds);
//
//    int updateByProbeId(ProbeModel record);
//
//    int countQueryByProbeName(@Param("probeName") String probeName);
//
//    int countQueryModifyByProbeName(@Param("probeName") String probeName, @Param("probeId") String probeId);
//
//    int updateProbeStatus(@Param("probeId") String probeId, @Param("probeStatus") ProbeStatus probeStatus);
//
//    List<ProbeModel> queryAllProbes();
//}
