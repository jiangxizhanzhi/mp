package cn.cyberpeace.mp.devopslog.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopslog.po.LogReportPO;
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
