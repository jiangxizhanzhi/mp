package cn.cyberpeace.mp.front.admin.service;

import cn.cyberpeace.mp.front.admin.model.NoticeTypeModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface NoticeTypeService {
    /**
     * 查询所有系统公告类型
     * @return
     */
    List<NoticeTypeModel> findAll();
}
