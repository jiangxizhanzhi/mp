 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FsRemoteFileServiceImpl.java.
 // File size 428.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
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
