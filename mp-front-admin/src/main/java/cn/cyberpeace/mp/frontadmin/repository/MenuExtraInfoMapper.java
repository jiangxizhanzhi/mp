 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MenuExtraInfoMapper.java.
 // File size 657.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.frontadmin.model.MenuExtraInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/17 11:13
 */
@Mapper
@Repository
@ModelClass(MenuExtraInfoModel.class)
public interface MenuExtraInfoMapper extends BaseModelMapper<MenuExtraInfoModel> {

    List<MenuExtraInfoModel> listAll();

    int deleteAll();
}
