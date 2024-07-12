 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneNodeConnectionMapper.java.
 // File size 745.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneAreaNodeConnectionModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneNodeConnectionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName SceneNodeConnectionMapper
 * @Description 场景节点连接映射器
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneNodeConnectionMapper
 */
@Mapper
@Repository
@ModelClass(SceneAreaNodeConnectionModel.class)
public interface SceneNodeConnectionMapper
        extends BaseSceneNodeConnectionMapper<SceneAreaNodeConnectionModel> {
}
