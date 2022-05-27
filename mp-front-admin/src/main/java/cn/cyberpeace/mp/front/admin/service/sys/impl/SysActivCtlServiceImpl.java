package cn.cyberpeace.mp.front.admin.service.sys.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.front.admin.model.SysActivCtlModel;
import cn.cyberpeace.mp.front.admin.repository.SysActivCtlMapper;
import cn.cyberpeace.mp.front.admin.service.fields.SysActivCtlFields;
import cn.cyberpeace.mp.front.admin.service.sys.SysActivCtlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/15 11:13
 */
@Service
@Slf4j
public class SysActivCtlServiceImpl extends BaseModelServiceImpl<SysActivCtlModel, SysActivCtlMapper, SysActivCtlFields, QueryParams>
        implements SysActivCtlService {

    @Autowired
    public SysActivCtlServiceImpl(SysActivCtlMapper modelMapper, SysActivCtlFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public SysActivCtlModel selectUnused() {
        return getModelMapper().selectUnused();
    }

    @Override
    public SysActivCtlModel selectValid() {
        return getModelMapper().selectValid();
    }

    @Override
    public void invalidOtherExceptOne(String exceptedId) {
        log.info("invalidOtherExceptOne exceptedId:{}", exceptedId);
        getModelMapper().invalidOtherExceptOne(exceptedId);
    }
}
