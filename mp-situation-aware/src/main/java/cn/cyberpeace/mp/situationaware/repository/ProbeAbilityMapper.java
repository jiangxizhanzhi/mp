package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.ProbeAbilityModel;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeAbilityDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProbeAbilityMapper继承基类
 */
@Mapper
@Repository
@ModelClass(ProbeAbilityModel.class)
public interface ProbeAbilityMapper extends BaseModelMapper<ProbeAbilityModel> {

    List<ProbeAbilityDto> listProbeAbilityDtos(@Param("condition") ProbeSearchCondition condition);

}
