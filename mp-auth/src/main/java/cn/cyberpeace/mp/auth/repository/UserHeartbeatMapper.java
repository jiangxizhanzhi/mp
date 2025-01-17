 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserHeartbeatMapper.java.
 // File size 712.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.bo.user.UserOlReportCntBo;
import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(UserHeartbeatModel.class)
public interface UserHeartbeatMapper extends BaseModelMapper<UserHeartbeatModel> {
}
