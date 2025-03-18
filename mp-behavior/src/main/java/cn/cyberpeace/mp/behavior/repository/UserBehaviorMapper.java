 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkBehaviorMapper.java.
 // File size 634.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.behavior.model.UserBehaviorModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

 /**
  * @ClassName NetworkBehaviorMapper
  * @Description
  * @Author maojun
  * @Date 2024/07/02
  * @Version 1.0.0
  * @see BaseModelMapper
  */
 @Mapper
 @Repository
 @ModelClass(UserBehaviorModel.class)
 public interface UserBehaviorMapper extends BaseModelMapper<UserBehaviorModel> {
 }
