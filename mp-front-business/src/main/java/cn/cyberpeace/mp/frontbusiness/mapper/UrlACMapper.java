 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlACMapper.java.
 // File size 931.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.frontbusiness.dto.UrlAccessCtlDto;
import cn.cyberpeace.mp.frontbusiness.model.UrlACModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 19:50
 */
@Mapper
@Repository
@ModelClass(UrlACModel.class)
public interface UrlACMapper extends BaseModelMapper<UrlACModel> {

    UrlAccessCtlDto getById(@Param("id") String id);

    List<UrlAccessCtlDto> listAllUrlAccessCtlDtos();

    List<UrlAccessCtlDto> listAllUrlAccessCtlPatternDtos();

    List<UrlAccessCtlDto> listByTargetIds(@Param("targetIds") List<String> targetIds);

}
