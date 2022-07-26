package cn.cyberpeace.mp.front.admin.repository;

import cn.cyberpeace.mp.front.admin.model.NoticeTypeModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Mapper
@Repository
public interface NoticeTypeMapper {
    /** 查询所有系统公告类型
     * @return
     */
    List<NoticeTypeModel> selectAll();
}
