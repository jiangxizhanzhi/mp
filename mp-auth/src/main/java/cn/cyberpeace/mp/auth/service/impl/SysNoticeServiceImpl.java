package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.notice.SysNoticeRequest;
import cn.cyberpeace.mp.auth.model.SysNoticeModel;
import cn.cyberpeace.mp.auth.repository.SysNoticeMapper;
import cn.cyberpeace.mp.auth.service.SysNoticeService;
import cn.cyberpeace.mp.auth.service.fields.SysNoticeFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.RelationSupport;

@Service
@Slf4j
public class SysNoticeServiceImpl extends BaseModelServiceImpl<SysNoticeModel, SysNoticeMapper, SysNoticeFields, QueryParams>
        implements SysNoticeService {

    @Autowired
    public SysNoticeServiceImpl(SysNoticeMapper modelMapper, SysNoticeFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public SysNoticeModel create(SysNoticeRequest sysNoticeRequest) {
        SysNoticeModel noticeModel = new SysNoticeModel();
        BeanUtils.copyProperties(sysNoticeRequest, noticeModel);

        ObjServiceResult<SysNoticeModel> result = create(noticeModel);
        return result.getSource().getObj();
    }


}
