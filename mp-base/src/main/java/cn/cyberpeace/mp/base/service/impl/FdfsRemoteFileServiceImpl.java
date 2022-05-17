package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FdfsRemoteFileServiceImpl extends BaseRemoteFileServiceImpl {

    @Autowired
    public FdfsRemoteFileServiceImpl(FdfsCommon fdfsCommon) {
        setFileClient(FdfsCommon.fdfsClient);
    }

}
