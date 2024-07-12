 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AdminMapper.java.
 // File size 524.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @ClassName AdminMapper
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/6 14:23
 * @Version 1.0.0
 */
@Mapper
@Repository
public interface AdminMapper {

    /**
     * getConfigValue
     *
     * @param keyName
     * @return
     */
    String getConfigValue(@Param("keyName") String keyName);

}
