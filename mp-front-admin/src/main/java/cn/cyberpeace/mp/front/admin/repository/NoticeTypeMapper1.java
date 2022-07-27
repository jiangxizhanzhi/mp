package cn.cyberpeace.mp.front.admin.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.front.admin.model.NoticeTypeModel;
import cn.cyberpeace.mp.front.admin.model.SysNoticeModel;
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
@ModelClass(NoticeTypeModel.class)
public interface NoticeTypeMapper1 extends BaseModelMapper<NoticeTypeModel> {

    /**
     * 查询所有系统公告类型
     * @return
     */
    List<NoticeTypeModel> listAll();
}
