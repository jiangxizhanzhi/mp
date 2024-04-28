package cn.cyberpeace.mp.common.repository;

import cn.cyberpeace.mp.base.bo.MpSysCodeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommonCodeMapper{

    List<MpSysCodeEnum> getNextLevelDataByCode(String code);

    List<MpSysCodeEnum> getNextAllDataByCode(String code);
}
