 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MenuExtraInfoMapper.java.
 // File size 855.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.frontadmin.model.MenuExtraInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName MenuExtraInfoMapper
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/12/17 11:13
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(MenuExtraInfoModel.class)
public interface MenuExtraInfoMapper extends BaseModelMapper<MenuExtraInfoModel> {

    /**
     * listAll
     *
     * @return
     */
    List<MenuExtraInfoModel> listAll();

    /**
     * deleteAll
     *
     * @return int
     */
    int deleteAll();
}
