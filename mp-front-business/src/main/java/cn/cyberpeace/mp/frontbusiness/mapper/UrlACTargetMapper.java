 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlACTargetMapper.java.
 // File size 559.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.frontbusiness.model.UrlACTargetModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 19:50
 */
@Mapper
@Repository
@ModelClass(UrlACTargetModel.class)
public interface UrlACTargetMapper extends BaseModelMapper<UrlACTargetModel> {

}
