package cn.cyberpeace.mp.front.admin.service.sys.impl;

import cn.cyberpeace.mp.base.repository.condition.Condition;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.front.admin.model.NoticeTypeModel;
import cn.cyberpeace.mp.front.admin.repository.NoticeTypeMapper1;
import cn.cyberpeace.mp.front.admin.service.fields.NoticeTypeFields;
import cn.cyberpeace.mp.front.admin.service.sys.NoticeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class NoticeTypeServiceImpl extends BaseModelServiceImpl<NoticeTypeModel, NoticeTypeMapper1, NoticeTypeFields, QueryParams>
        implements NoticeTypeService {

    @Autowired
    public NoticeTypeServiceImpl(NoticeTypeMapper1 noticeTypeMapper1, NoticeTypeFields noticeTypeFields) {
        setModelMapper(noticeTypeMapper1);
        setModelFields(noticeTypeFields);
    }

    @Override
    public List<NoticeTypeModel> findAll() {
        return getModelMapper().list(new HumanCondition());
    }
}
