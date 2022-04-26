package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ProbeParamModel.class)
public interface ProbeParamMapper extends BaseModelMapper<ProbeParamModel> {

    int deleteByPrimaryKey(Integer pk);

    int insert(ProbeParamModel record);

    int insertSelective(ProbeParamModel record);

    ProbeParamModel selectByPrimaryKey(Integer pk);

    int updateByPrimaryKeySelective(ProbeParamModel record);

    int updateByPrimaryKey(ProbeParamModel record);

    int deleteByProbeIdAndTemplateId(@Param("probeId") String probeId, @Param("templateId") String templateId);
}
