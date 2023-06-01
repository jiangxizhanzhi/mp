package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysDeployLogModel;
import cn.cyberpeace.mp.frontadmin.repository.SysDeployLOgMapper;
import cn.cyberpeace.mp.frontadmin.service.fields.SysDeployLogFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysDeployLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/05/31 16:13
 */
@Service
@Slf4j
public class SysDeployLogServiceImpl extends BaseModelServiceImpl<SysDeployLogModel, SysDeployLOgMapper, SysDeployLogFields, QueryParams>
        implements SysDeployLogService {

    @Autowired
    public SysDeployLogServiceImpl(SysDeployLOgMapper modelMapper, SysDeployLogFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
