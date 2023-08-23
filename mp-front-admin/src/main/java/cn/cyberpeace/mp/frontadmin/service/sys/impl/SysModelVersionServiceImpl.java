package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysModelVersionModel;
import cn.cyberpeace.mp.frontadmin.repository.SysModelVersionMapper;
import cn.cyberpeace.mp.frontadmin.service.fields.SysModelVersionFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysModelVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/8/23 15:36
 */
@Service
@Slf4j
public class SysModelVersionServiceImpl extends BaseModelServiceImpl<SysModelVersionModel, SysModelVersionMapper, SysModelVersionFields, QueryParams>
        implements SysModelVersionService {

    @Autowired
    private SysModelVersionMapper sysModelVersionMapper;

    @Override
    public List<SysModelVersionModel> listSysVersion() {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(SysModelVersionModel::getVisible, true);
        List<SysModelVersionModel> sysModelVersionModels = sysModelVersionMapper.list(humanCondition);
        return sysModelVersionModels;
    }

}
