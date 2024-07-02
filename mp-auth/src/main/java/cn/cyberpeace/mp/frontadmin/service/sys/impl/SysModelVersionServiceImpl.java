package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysModelVersionModel;
import cn.cyberpeace.mp.frontadmin.repository.SysModelVersionMapper;
import cn.cyberpeace.mp.frontadmin.response.SysVersionResponse;
import cn.cyberpeace.mp.frontadmin.service.fields.SysModelVersionFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysModelVersionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName SysModelVersionServiceImpl
 * @Description
 * @Author guquan
 * @Date 2023/8/23 15:36
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 * @see SysModelVersionService
 */
@Service
@Slf4j
public class SysModelVersionServiceImpl extends BaseModelServiceImpl<SysModelVersionModel, SysModelVersionMapper, SysModelVersionFields, QueryParams>
        implements SysModelVersionService {

    @Autowired
    private SysModelVersionMapper sysModelVersionMapper;

    private static final String SYS_NAME = "SYS";

    /**
     * listSysVersion
     *
     * @return
     */
    @Override
    public SysVersionResponse listSysVersion() {
        SysVersionResponse sysVersionResponse = new SysVersionResponse();
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(SysModelVersionModel::getVisible, true);
        humanCondition.filterNotEqual(SysModelVersionModel::getModelName, SYS_NAME);
        List<SysModelVersionModel> sysModelVersionModels = sysModelVersionMapper.list(humanCondition);
        sysVersionResponse.setModelVersions(sysModelVersionModels);
        HumanCondition humanCondition2 = new HumanCondition();
        humanCondition2.filterEqual(SysModelVersionModel::getVisible, true);
        humanCondition2.filterEqual(SysModelVersionModel::getModelName, SYS_NAME);
        List<SysModelVersionModel> sysModelVersionModels2 = sysModelVersionMapper.list(humanCondition2);
        if (CollectionUtils.isNotEmpty(sysModelVersionModels2)) {
            sysVersionResponse.setSysVersion(sysModelVersionModels2.get(0));
        }
        return sysVersionResponse;
    }

}
