package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceCountByReferenceTypeModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(CsceneInstanceModel.class)
public interface CsceneInstanceMapper extends BaseCsceneMapper<CsceneInstanceModel> {

    /**
     * 根据引用类型分组统计
     * @param events
     * @return
     */
    List<CsceneInstanceCountByReferenceTypeModel> countGroupByReferenceType(@Param("events") List<Integer> events);
}
