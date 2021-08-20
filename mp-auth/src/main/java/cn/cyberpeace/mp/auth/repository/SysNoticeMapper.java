package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.SysNoticeModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(SysNoticeModel.class)
public interface SysNoticeMapper extends BaseModelMapper<SysNoticeModel> {

    List<SysNoticeModel> listNotices(@Param("title") String title, @Param("ids") List<String> ids,
                                     @Param("orgs") List<String> orgs, @Param("roles") List<String> roles,
                                     @Param("publisher") String publisher, @Param("type") Integer type);


    List<SysNoticeModel> listCurrentUserNotices(@Param("title") String title, @Param("ids") List<String> ids,
                                     @Param("orgs") List<String> orgs, @Param("roles") List<String> roles,
                                     @Param("publisher") String publisher, @Param("type") Integer type, @Param("userId") String userId);

}
