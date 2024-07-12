 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneConfigPieceMapper.java.
 // File size 654.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneConfigPieceModel;
import cn.cyberpeace.mp.scene.repository.base.BaseScenePieceMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName SceneConfigPieceMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseScenePieceMapper
 */
@Mapper
@Repository
@ModelClass(SceneConfigPieceModel.class)
public interface SceneConfigPieceMapper extends BaseScenePieceMapper<SceneConfigPieceModel> {
}
