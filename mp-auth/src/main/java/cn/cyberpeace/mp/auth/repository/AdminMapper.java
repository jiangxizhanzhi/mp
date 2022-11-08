package cn.cyberpeace.mp.auth.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/6 14:23
 */
@Mapper
@Repository
public interface AdminMapper {

    String getConfigValue(@Param("keyName") String keyName);

}
