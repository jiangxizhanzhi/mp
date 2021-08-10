package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.SysNoticeGroupModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(SysNoticeGroupModel.class)
public interface SysNoticeGroupMapper extends BaseModelMapper<SysNoticeGroupModel> {

    int deleteByNoticeIds(@Param("noticeIds") List<String> noticeIds);

    int deleteByOwner(@Param("ownerId") String ownerId);

}
