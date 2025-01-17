 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AdminMapper.java.
 // File size 393.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
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
