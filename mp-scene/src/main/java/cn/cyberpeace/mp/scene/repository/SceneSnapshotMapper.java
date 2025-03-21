 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneAreaMapper.java.
 // File size 616.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.repository;

 import cn.cyberpeace.mp.base.repository.BaseModelMapper;
 import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
 import cn.cyberpeace.mp.scene.model.SceneSnapshotModel;
 import cn.cyberpeace.mp.scene.repository.base.BaseSceneAreaMapper;
 import org.apache.ibatis.annotations.Mapper;
 import org.springframework.stereotype.Repository;

 /**
  * @ClassName SceneAreaMapper
  * @Description
  * @Author maojun
  * @Date 2024/07/02
  * @Version 1.0.0
  * @see BaseSceneAreaMapper
  */
 @Mapper
 @Repository
 @ModelClass(SceneSnapshotModel.class)
 public interface SceneSnapshotMapper extends BaseModelMapper<SceneSnapshotModel> {
 }
