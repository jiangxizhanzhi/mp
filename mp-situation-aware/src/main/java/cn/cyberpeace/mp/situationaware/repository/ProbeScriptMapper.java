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
    












    int deleteByProbeId(String probeId);

    String getChecksumByProbeId(String probeId);

    ProbeScriptModel getScriptModelByProbeId(String probeId);
}
