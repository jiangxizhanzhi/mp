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


    @Deprecated
    List<SysNoticeModel> listCurrentUserNotices(@Param("title") String title, @Param("ids") List<String> ids,
                                                @Param("orgs") List<String> orgs, @Param("roles") List<String> roles,
                                                @Param("publisher") String publisher, @Param("type") Integer type, @Param("userId") String userId);


    /**
     * 管理员消息推送列表
     *
     * @param userId
     * @return
     */
    List<SysNoticeModel> listNoticeMessageByCurrentAdminUser(@Param("userId") String userId);

    /**
     * 管理员消息推送列表总数
     *
     * @param userId
     * @return
     */
    int getUnreadCountByCurrentAdminUser(@Param("userId") String userId);

    /**
     * 普通用户消息推送列表
     *
     * @param orgs
     * @param roles
     * @param userId
     * @return
     */
    List<SysNoticeModel> listNoticeMessageByCurrentNormalUser(@Param("orgs") List<String> orgs,
                                                              @Param("roles") List<String> roles,
                                                              @Param("userId") String userId);


    /**
     * 普通用户消息推送列表总数
     *
     * @param orgs
     * @param roles
     * @param userId
     * @return
     */
    int getUnreadCountByCurrentNormalUser(@Param("orgs") List<String> orgs,
                                          @Param("roles") List<String> roles,
                                          @Param("userId") String userId);


}
