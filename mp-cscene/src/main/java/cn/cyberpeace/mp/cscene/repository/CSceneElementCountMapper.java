package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CSceneElementCountModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CSceneElementCountModel.class)
public interface CSceneElementCountMapper extends BaseModelMapper<CSceneElementCountModel> {
    int updateElementCount(int count, int limit);
}
