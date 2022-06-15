package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.service.LogFileService;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * 云平台日志模块
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
public class CpCloudLogFileServiceImpl implements LogFileService {

    @Override
    public ServiceResult list() {
        return null;
    }

    @Override
    public ByteArrayInputStream download() {
        return null;
    }

    @Override
    public ByteArrayInputStream download(List<String> logFileNames) {
        return null;
    }
}
