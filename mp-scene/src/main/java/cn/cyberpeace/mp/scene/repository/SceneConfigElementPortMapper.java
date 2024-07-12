 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneConfigElementPortMapper.java.
 // File size 702.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementPortMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName SceneConfigElementPortMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementPortMapper
 */
@Mapper
@Repository
@ModelClass(SceneConfigElementPortModel.class)
public interface SceneConfigElementPortMapper extends BaseSceneElementPortMapper<SceneConfigElementPortModel> {
}
