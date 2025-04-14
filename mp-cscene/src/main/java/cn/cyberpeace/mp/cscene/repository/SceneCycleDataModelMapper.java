package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneAuthModel;
import cn.cyberpeace.mp.cscene.model.SceneCycleDataModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
@ModelClass(SceneCycleDataModel.class)
public interface SceneCycleDataModelMapper extends BaseModelMapper<SceneCycleDataModel> {
}
