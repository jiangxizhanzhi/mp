package cn.cyberpeace.mp.auth.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 共性平台方法
 */
@Mapper
@Repository
public interface GXPTUserMapper {

    void updateUser(@Param("id") String id, @Param("resultMap") Map<String, Object> resultMap);

    String getInfoById(String userId);
}
