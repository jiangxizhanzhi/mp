package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.common.LfsCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LfsRemoteFileServiceImpl extends BaseRemoteFileServiceImpl {

    @Autowired
    public LfsRemoteFileServiceImpl(LfsCommon lfsCommon) {
        setFileClient(LfsCommon.lfsClient);
    }

}
