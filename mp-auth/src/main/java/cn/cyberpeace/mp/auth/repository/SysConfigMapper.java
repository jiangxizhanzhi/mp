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

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(SysConfigModel.class)
public interface SysConfigMapper extends BaseModelMapper<SysConfigModel> {

    List<SysConfigModel> selectAll();

    SysConfigModel selectByKey(@Param("keyName") String keyName);

    int updateByKeyName(@Param("key") String key, @Param("value") String value);


}
