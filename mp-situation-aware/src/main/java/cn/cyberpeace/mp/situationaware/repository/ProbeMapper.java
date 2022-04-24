package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(ProbeModel.class)
public interface ProbeMapper extends BaseModelMapper<ProbeModel> {
    int deleteByPrimaryKey(Integer pk);

    int insert(ProbeModel record);

    int insertSelective(ProbeModel record);

    ProbeModel selectByPrimaryKey(Integer pk);

    int updateByPrimaryKeySelective(ProbeModel record);

    int updateByPrimaryKey(ProbeModel record);

    List<ProbeModel> listByIds(@Param("ids") List<String> ids);
}
