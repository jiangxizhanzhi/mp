package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.common.FsCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FsRemoteFileServiceImpl extends BaseRemoteFileServiceImpl {

    @Autowired
    public FsRemoteFileServiceImpl(FsCommon fsCommon) {
        setFileClient(FsCommon.fileClient);
    }

}
