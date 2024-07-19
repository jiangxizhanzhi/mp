/**
 * 路由网络映射器接口，继承自BaseModelMapper，用于对RouteNetworkModel模型进行数据库操作。
 * 通过MyBatis的@Mapper注解，使其作为一个Mapper接口被Spring管理。
 */
// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name CsceneElementSoftVersionMapper.java.
// File size 947.
// File create date Fri Jul 12 12:55:22 CST 2024.
// File update date Fri Jul 12 12:55:22 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincloud.model.RouteNetworkModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(RouteNetworkModel.class)
public interface RouterNetworkMapper extends BaseModelMapper<RouteNetworkModel> {

}
