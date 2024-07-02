package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneElementStat;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName CsceneMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCsceneMapper
 */
@Mapper
@Repository
@ModelClass(CsceneModel.class)
public interface CsceneMapper extends BaseCsceneMapper<CsceneModel> {
    /**
     * statGroupByType
     *
     * @param sceneIds
     * @param cpu
     * @param ram
     * @param disk
     * @return
     */
    List<CsceneElementStat> statGroupByType(@Param("ids") String[] sceneIds, @Param("cpu") int cpu, @Param("ram") int ram, @Param("disk") int disk);

    /**
     * statGroupByFlow
     *
     * @param sceneIds
     * @param cpu
     * @param ram
     * @param disk
     * @return
     */
    List<CsceneElementStat> statGroupByFlow(@Param("ids") String[] sceneIds, @Param("cpu") int cpu, @Param("ram") int ram, @Param("disk") int disk);
}
