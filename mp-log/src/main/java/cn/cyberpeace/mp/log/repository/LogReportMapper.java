 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogReportMapper.java.
 // File size 475.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.log.po.LogReportPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 日志上报管理
 */
@Mapper
@Repository
@ModelClass(LogReportPO.class)
public interface LogReportMapper extends BaseModelMapper<LogReportPO> {

}
