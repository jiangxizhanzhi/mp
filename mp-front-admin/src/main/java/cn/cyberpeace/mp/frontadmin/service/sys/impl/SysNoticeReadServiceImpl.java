package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.constant.AdminConstant;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeReadModel;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeReadMapper;
import cn.cyberpeace.mp.frontadmin.service.SysNoticeReadService;
import cn.cyberpeace.mp.frontadmin.service.fields.SysNoticeReadFields;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class SysNoticeReadServiceImpl extends BaseModelServiceImpl<SysNoticeReadModel, SysNoticeReadMapper, SysNoticeReadFields, QueryParams>
    implements SysNoticeReadService {

    @Autowired
    public SysNoticeReadServiceImpl(SysNoticeReadMapper sysNoticeReadMapper) {
        setModelMapper(sysNoticeReadMapper);
    }

    @Override
    public SysNoticeReadModel selectByUserIdAndNoticeId(String user, String noticeId) {
        HumanCondition condition = new HumanCondition();
        if (StringUtils.isNotBlank(user)) {
            condition.filterEqual(SysNoticeReadModel::getUser, user);
        }
        if (StringUtils.isNotBlank(noticeId)) {
            condition.filterEqual(SysNoticeReadModel::getNoticeId, noticeId);
        }
        SysNoticeReadModel model = getModelMapper().first(condition);
        return model;
    }

    @Override
    public int increaseReadCount(String id) {
        HumanCondition selectCondition = new HumanCondition()
                .filterEqual(SysNoticeReadModel::getId, id);
        SysNoticeReadModel model = getModelMapper().first(selectCondition);
        SysNoticeReadModel newModel = new SysNoticeReadModel();
        newModel.setIsReaded(1);
        newModel.setReadCount(model.getReadCount() + 1);
        HumanCondition updateCondition = new HumanCondition()
                .filterEqual(SysNoticeReadModel::getId, id);
        int rows = getModelMapper().filterUpdate(newModel, updateCondition);
        return rows;
    }

    @Override
    public int countIsReadedOrIsUnPush(String user) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(SysNoticeReadModel::getUser, user);
        condition.filterOrStart()
                .orEqual(SysNoticeReadModel::getIsReaded, SysNoticeReadModel.NOTICE_READED)
                .orEqual(SysNoticeReadModel::getIsNeedPush, SysNoticeReadModel.NOTICE_NEED_PUSH)
                .filterOrEnd();
        condition.selectAs(Count.f(SysNoticeReadModel::getNoticeId).distinct(), AdminConstant.SYS_NOTICE_READ_COUNT_DISTINCT_NOTICEID);
        Map<String, Object> stringObjectMap = getModelMapper().originFirst(condition);
        return ((Long) stringObjectMap.get(AdminConstant.SYS_NOTICE_READ_COUNT_DISTINCT_NOTICEID)).intValue();
    }
}
