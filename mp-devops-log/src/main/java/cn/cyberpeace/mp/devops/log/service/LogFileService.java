package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
public interface LogFileService {

    /**
     * 列表
     * @return
     */
    ServiceResult list();

    /**
     * 下载
     * @return
     */
    ByteArrayInputStream download();

    /**
     * 下载
     * @param logFileNames
     * @return
     */
    ByteArrayInputStream download(List<String> logFileNames);
}
