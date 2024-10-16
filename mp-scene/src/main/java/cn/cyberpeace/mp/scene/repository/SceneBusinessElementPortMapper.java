// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name SceneMapper.java.
// File size 584.
// File create date Fri Jul 12 12:55:30 CST 2024.
// File update date Fri Jul 12 12:55:30 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneBusinessElementModel;
import cn.cyberpeace.mp.scene.model.SceneBusinessElementPortModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(SceneBusinessElementPortModel.class)
public interface SceneBusinessElementPortMapper extends BaseModelMapper<SceneBusinessElementPortModel> {
}
