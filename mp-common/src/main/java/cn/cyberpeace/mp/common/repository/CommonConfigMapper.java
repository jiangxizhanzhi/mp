package cn.cyberpeace.mp.common.repository;

import cn.cyberpeace.mp.base.bo.MpSysUrlConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CommonConfigMapper {

    MpSysUrlConfig getUrlByKey(String code);
}
