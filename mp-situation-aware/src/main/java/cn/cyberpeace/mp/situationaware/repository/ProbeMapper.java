package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
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
    /**
     * updateByPrimaryKeySelective
     *
     * @param record
     * @return int
     */
    int updateByPrimaryKeySelective(ProbeModel record);
//
//    int updateByPrimaryKey(ProbeModel record);

    /**
     * getById
     *
     * @param id
     * @return
     */
    ProbeDto getById(@Param("id") String id);

//    List<ProbeDto> listProbes(@Param("name") String name, @Param("types") List<Integer> types,
//                                 @Param("osTypes") List<Integer> osTypes, @Param("catagories") List<String> catagories,
//                                 @Param("scriptTypes") List<Integer> scriptTypes);

    /**
     * listProbeDtos
     *
     * @param condition
     * @return
     */
    List<ProbeDto> listProbeDtos(@Param("condition") ProbeSearchCondition condition);

    /**
     * getByName
     *
     * @param name
     * @return 探针实体
     */
    ProbeModel getByName(String name);

}

