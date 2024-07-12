 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserLogService.java.
 // File size 1285.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.log.controller.OperateLogController;
import cn.cyberpeace.mp.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.log.response.OperationLogPageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName UserLogService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Service
@Slf4j
public class UserLogService {

    @Autowired
    private OperateLogController operateLogController;

    /**
     * listOpLog
     *
     * @param request
     * @return
     */
    public List<OperationLogPageResponse> listOpLog(OperationLogPageRequest request) {
        ListObjServiceResult<OperationLogPageResponse> response = operateLogController.page(request);
        if (Objects.nonNull(response.getData().getList())) {
            List<OperationLogPageResponse> list = response.getSource().getList();
            return list;
        }
        return new ArrayList<>();
    }


}
