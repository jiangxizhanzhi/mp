package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.SysNoticeModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(SysNoticeModel.class)
public interface SysNoticeMapper extends BaseModelMapper<SysNoticeModel> {

    List<SysNoticeModel> listNotices(@Param("title") String title, @Param("ids") List<String> ids,
                                     @Param("orgs") List<String> orgs, @Param("roles") List<String> roles,
                                     @Param("publisher") String publisher, @Param("type") Integer type);

}
