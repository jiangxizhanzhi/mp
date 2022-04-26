package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.dto.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.ProbeSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(ProbeModel.class)
public interface ProbeMapper extends BaseModelMapper<ProbeModel> {
//    int deleteByPrimaryKey(Integer pk);
//
//    int insert(ProbeModel record);
//
//    int insertSelective(ProbeModel record);
//
//    ProbeModel selectByPrimaryKey(Integer pk);
//
//    int updateByPrimaryKeySelective(ProbeModel record);
//
//    int updateByPrimaryKey(ProbeModel record);

    ProbeDto getById(@Param("id") String id);

//    List<ProbeDto> listProbes(@Param("name") String name, @Param("types") List<Integer> types,
//                                 @Param("osTypes") List<Integer> osTypes, @Param("catagories") List<String> catagories,
//                                 @Param("scriptTypes") List<Integer> scriptTypes);

    List<ProbeDto> listProbes(@Param("condition") ProbeSearchCondition condition);


}

