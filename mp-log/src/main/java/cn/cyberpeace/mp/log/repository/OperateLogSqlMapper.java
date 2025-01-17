 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OperateLogSqlMapper.java.
 // File size 534.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.log.model.OperateLogSqlModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/13
 */
@Mapper
@Repository
@ModelClass(OperateLogSqlModel.class)
public interface OperateLogSqlMapper extends BaseModelMapper<OperateLogSqlModel> {
}
