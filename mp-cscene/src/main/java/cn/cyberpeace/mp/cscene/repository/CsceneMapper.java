package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneElementStat;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneMapper;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(CsceneModel.class)
public interface CsceneMapper extends BaseCsceneMapper<CsceneModel> {
    List<CsceneElementStat> statGroupByType(@Param("ids") String[] sceneIds, @Param("cpu") int cpu, @Param("ram") int ram, @Param("disk") int disk);
}
