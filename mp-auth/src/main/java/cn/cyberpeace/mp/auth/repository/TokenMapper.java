 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TokenMapper.java.
 // File size 727.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.TokenModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/6 14:23
 */
@Mapper
@Repository
@ModelClass(TokenModel.class)
public interface TokenMapper extends BaseModelMapper<TokenModel> {

    TokenModel getByJwt(@Param("jwt") String jwt);

    List<TokenModel> getByLoginToken(@Param("loginToken") String loginToken);

}
