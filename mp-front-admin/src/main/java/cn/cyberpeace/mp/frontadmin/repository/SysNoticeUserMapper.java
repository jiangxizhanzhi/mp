 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeUserMapper.java.
 // File size 563.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeUserModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/9/20 11:13
 */
@Mapper
@Repository
@ModelClass(SysNoticeUserModel.class)
public interface SysNoticeUserMapper extends BaseModelMapper<SysNoticeUserModel> {
}
