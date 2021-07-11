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








    int updateByPrimaryKeySelective(ProbeModel record);



    ProbeDto getById(@Param("id") String id);





    List<ProbeDto> listProbeDtos(@Param("condition") ProbeSearchCondition condition);

    ProbeModel getByName(String name);

}

