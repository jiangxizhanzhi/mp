package cn.cyberpeace.mp.front.admin.repository;

import cn.cyberpeace.mp.front.admin.model.SysNoticeReadModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(SysNoticeReadModel.class)
public interface SysNoticeReadMapper extends BaseModelMapper<SysNoticeReadModel> {

    SysNoticeReadModel selectByUserIdAndNoticeId(@Param("user") String user, @Param("noticeId") String noticeId);

    int increaseReadCount(@Param("id") String id);

    int countIsReadedOrIsUnPush(@Param("user") String user);

}
