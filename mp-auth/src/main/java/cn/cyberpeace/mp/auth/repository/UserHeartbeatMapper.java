package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(UserHeartbeatModel.class)
public interface UserHeartbeatMapper extends BaseModelMapper<UserHeartbeatModel> {



}
