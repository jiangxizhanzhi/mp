package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.scene.domain.SceneConfigDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SceneConfigMapper {
    List<SceneConfigDO> findAll();
}
