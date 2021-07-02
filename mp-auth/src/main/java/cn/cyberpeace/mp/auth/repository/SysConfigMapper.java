package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.auth.service.impl.SysConfigServiceImpl;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(SysConfigModel.class)
public interface SysConfigMapper extends BaseModelMapper<SysConfigModel> {

    @Select("select * form mp_sys_config")
    List<SysConfigModel> selectAll();

    @Select("select * form mp_sys_config where keyName = #{keyName}")
    SysConfigModel selectByKey(@Param("keyName") String keyName);

}
