package cn.cyberpeace.mp.front.admin.service.impl;

import cn.cyberpeace.mp.front.admin.model.NoticeTypeModel;
import cn.cyberpeace.mp.front.admin.repository.NoticeTypeMapper;
import cn.cyberpeace.mp.front.admin.service.NoticeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class NoticeTypeServiceImpl implements NoticeTypeService {

    @Autowired
    private NoticeTypeMapper noticeTypeMapper;

    @Override
    public List<NoticeTypeModel> findAll() {
        return noticeTypeMapper.selectAll();
    }
}
