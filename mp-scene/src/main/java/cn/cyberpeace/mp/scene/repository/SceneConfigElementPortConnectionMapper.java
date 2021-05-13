package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortConnectionModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(SceneConfigElementPortConnectionModel.class)
public interface SceneConfigElementPortConnectionMapper extends BaseModelMapper<SceneConfigElementPortConnectionModel> {
}
