package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(SysConfigModel.class)
public interface SysConfigMapper extends BaseModelMapper<SysConfigModel> {

    @Select("select * from mp_sys_config")
    List<SysConfigModel> selectAll();

    @Select("select * from mp_sys_config where key_name = #{keyName}")
    SysConfigModel selectByKey(@Param("keyName") String keyName);

    @Update("update mp_sys_config set value = #{value}, update_time = CURRENT_TIMESTAMP where key_name = #{key}")
    int updateByKeyName(@Param("key") String key, @Param("value") String value);


}
